package bruhcollective.itaysonlab.jetispot.core.collection

import android.util.Log
import bruhcollective.itaysonlab.jetispot.core.DeviceIdProvider
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpCollectionApi
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionRepository
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtist
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtistMetadata
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionTrack
import com.google.protobuf.ByteString
import com.spotify.collection2.v2.proto.Collection2V2
import com.spotify.extendedmetadata.ExtendedMetadata
import com.spotify.extendedmetadata.ExtensionKindOuterClass
import com.spotify.metadata.Metadata
import kotlinx.coroutines.*
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.*
import java.util.concurrent.Executors
import javax.inject.Inject

class SpCollectionManager @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val collectionApi: SpCollectionApi,
  private val dbRepository: LocalCollectionRepository
) {
  // it's important to use queuing here
  private val scopeDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
  private val scope = CoroutineScope(scopeDispatcher + SupervisorJob())

  private val pendingWriteOperations = mutableListOf<CollectionWriteOp>()

  sealed class CollectionWriteOp(val toSet: String) {
    class Add(val spId: String, toSet: String) : CollectionWriteOp(toSet) {
      val additionDate = (System.currentTimeMillis() / 1000L).toInt()
    }

    class Remove(val spId: String, toSet: String) : CollectionWriteOp(toSet)
  }

  private suspend fun processPendingWrites() {
    if (pendingWriteOperations.isEmpty()) return

    collectionApi.write(Collection2V2.WriteRequest.newBuilder().apply {
      username = spSessionManager.session.username()
      set = "collection"
      clientUpdateId = DeviceIdProvider.getRandomString(16)
      addAllItems(pendingWriteOperations.map {
        when (it) {
          is CollectionWriteOp.Add -> Collection2V2.CollectionItem.newBuilder().setUri(it.spId)
            .setAddedAt(it.additionDate).build()
          is CollectionWriteOp.Remove -> Collection2V2.CollectionItem.newBuilder().setUri(it.spId)
            .setIsRemoved(true).build()
        }
      })
    }.build())
  }

  // scans network collection for tracks, albums, pins and artists
  suspend fun scan() = withContext(scopeDispatcher) {
    performScan("collection")
    performScan("artist")
    // performScan("ylpin")
  }

  suspend fun artists() = withContext(scopeDispatcher) {
    performScanIfEmpty("artist")
    dbRepository.getArtists()
  }

  suspend fun albums() = withContext(scopeDispatcher) {
    performScanIfEmpty("collection")
    dbRepository.getAlbums()
  }

  private suspend fun performScanIfEmpty(of: String) {
    dbRepository.getCollection(of) ?: performScan(of)
  }

  /**
   * scan&add all info needed
   *
   * for "collection"
   * - fetch data (ID's: albums and tracks)
   * - fetch metadata (albums&tracks - title/artist/picture, also fetch artists to get genre information)
   * - save to DB
   *
   * for "artist" + "ylpin"
   * - fetch data, metadata and save to DB
   */
  private suspend fun performScan(of: String) {
    Log.d("SpColManager", "Performing scan of $of")

    val data = performPagingScan(of)

    Log.d("SpColManager", "Scan of $of completed [total = ${data.first.size}]")
  }

  private fun spotifyIdToKind(id: String) = when (id.split(":")[1]) {
    "track" -> ExtensionKindOuterClass.ExtensionKind.TRACK_V4
    "album" -> ExtensionKindOuterClass.ExtensionKind.ALBUM_V4
    "artist" -> ExtensionKindOuterClass.ExtensionKind.ARTIST_V4
    else -> ExtensionKindOuterClass.ExtensionKind.UNKNOWN_EXTENSION
  }

  private fun bytesToPicUrl(bytes: ByteString) = ImageId.fromHex(Utils.bytesToHex(bytes)).hexId()

  private suspend fun performPagingScan(of: String): Pair<List<Collection2V2.CollectionItem>, String> {
    val items = mutableListOf<Collection2V2.CollectionItem>()

    val syncToken: String
    var pToken = ""

    while (true) {
      Log.d("SpColManager", "Performing page request of $of [pToken = $pToken]")

      val page = collectionApi.paging(Collection2V2.PageRequest.newBuilder().apply {
        username = spSessionManager.session.username()
        set = of
        paginationToken = pToken
        limit = 300
      }.build())

      items.addAll(page.itemsList)

      Log.d(
        "SpColManager",
        "Performing page metadata request of $of [count = ${page.itemsList.size}]"
      )

      val requests = mutableListOf<ExtendedMetadata.EntityRequest>()

      page.itemsList.forEach { ci ->
        val kind = spotifyIdToKind(ci.uri)

        if (kind != ExtensionKindOuterClass.ExtensionKind.UNKNOWN_EXTENSION) {
          requests.add(
            ExtendedMetadata.EntityRequest.newBuilder().setEntityUri(ci.uri)
              .addQuery(ExtendedMetadata.ExtensionQuery.newBuilder().setExtensionKind(kind).build())
              .build()
          )
        }
      }

      val metadata = UnpackedMetadataResponse(
        spSessionManager.session.api().getExtendedMetadata(
          ExtendedMetadata.BatchedEntityRequest.newBuilder().addAllEntityRequest(requests).build()
        ).extendedMetadataList
      )

      if (metadata.tracks.isNotEmpty()) {
        // also request artists to get genre data

        val mappedArtists = UnpackedMetadataResponse(spSessionManager.session.api()
          .getExtendedMetadata(
            ExtendedMetadata.BatchedEntityRequest.newBuilder().addAllEntityRequest(
              metadata.tracks.map {
                ExtendedMetadata.EntityRequest.newBuilder().setEntityUri(ArtistId.fromHex(Utils.bytesToHex(it.value.artistList.first().gid)).toSpotifyUri()).addQuery(
                  ExtendedMetadata.ExtensionQuery.newBuilder()
                    .setExtensionKind(ExtensionKindOuterClass.ExtensionKind.ARTIST_V4).build()
                ).build()
              }.distinctBy { it.entityUri }
            ).build()
          )
          .extendedMetadataList
        ).artists.map {
          CollectionArtistMetadata(
            Utils.bytesToHex(it.value.gid),
            it.value.genreList.joinToString("|")
          )
        }.toTypedArray()

        dbRepository.insertMetaArtists(*mappedArtists)
      }

      val mappedRequest = page.itemsList.associate { Pair(it.uri, it.addedAt) }

      dbRepository.insertTracks(*metadata.tracks.values.map { track ->
        CollectionTrack(
          id = TrackId.fromHex(Utils.bytesToHex(track.gid)).hexId(),
          uri = TrackId.fromHex(Utils.bytesToHex(track.gid)).toSpotifyUri(),
          name = track.name,
          albumId = Utils.bytesToHex(track.album.gid),
          albumName = track.album.name,
          mainArtistId = Utils.bytesToHex(track.artistList.first().gid),
          rawArtistsData = track.artistList.joinToString("|") { artist -> "${Utils.bytesToHex(artist.gid)}=${artist.name}" },
          hasLyrics = track.hasLyrics,
          isExplicit = track.explicit,
          duration = track.duration,
          picture = bytesToPicUrl(track.album.coverGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId),
          addedAt = mappedRequest[TrackId.fromHex(Utils.bytesToHex(track.gid)).toSpotifyUri()]!!
        )
      }.toTypedArray())

      dbRepository.insertAlbums(*metadata.albums.values.map { album ->
        CollectionAlbum(
          id = AlbumId.fromHex(Utils.bytesToHex(album.gid)).hexId(),
          uri = AlbumId.fromHex(Utils.bytesToHex(album.gid)).toSpotifyUri(),
          rawArtistsData = album.artistList.joinToString("|") { artist -> "${Utils.bytesToHex(artist.gid)}=${artist.name}" },
          name = album.name,
          picture = bytesToPicUrl(album.coverGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId),
          addedAt = mappedRequest[AlbumId.fromHex(Utils.bytesToHex(album.gid)).toSpotifyUri()]!!
        )
      }.toTypedArray())

      dbRepository.insertArtists(*metadata.artists.values.map { artist ->
        CollectionArtist(
          id = ArtistId.fromHex(Utils.bytesToHex(artist.gid)).hexId(),
          uri = ArtistId.fromHex(Utils.bytesToHex(artist.gid)).toSpotifyUri(),
          name = artist.name,
          picture = bytesToPicUrl(artist.portraitGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId),
          addedAt = mappedRequest[ArtistId.fromHex(Utils.bytesToHex(artist.gid)).toSpotifyUri()]!!
        )
      }.toTypedArray())

      if (!page.nextPageToken.isNullOrEmpty()) {
        // next page
        pToken = page.nextPageToken
      } else {
        // no more pages remain
        syncToken = page.syncToken
        break
      }
    }

    dbRepository.insertOrUpdateCollection(of, syncToken)
    return Pair(items, syncToken)
  }

  class UnpackedMetadataResponse(
    dataArray: List<ExtendedMetadata.EntityExtensionDataArray>
  ) {
    var tracks: Map<String, Metadata.Track> = mapOf()
      private set

    var artists: Map<String, Metadata.Artist> = mapOf()
      private set

    var albums: Map<String, Metadata.Album> = mapOf()
      private set

    init {
      dataArray.forEach { arr ->
        when (arr.extensionKind) {
          ExtensionKindOuterClass.ExtensionKind.TRACK_V4 -> {
            tracks = arr.extensionDataList.associate {
              Pair(
                it.entityUri,
                Metadata.Track.parseFrom(it.extensionData.value)
              )
            }
          }

          ExtensionKindOuterClass.ExtensionKind.ALBUM_V4 -> {
            albums = arr.extensionDataList.associate {
              Pair(
                it.entityUri,
                Metadata.Album.parseFrom(it.extensionData.value)
              )
            }
          }

          ExtensionKindOuterClass.ExtensionKind.ARTIST_V4 -> {
            artists = arr.extensionDataList.associate {
              Pair(
                it.entityUri,
                Metadata.Artist.parseFrom(it.extensionData.value)
              )
            }
          }
        }
      }
    }
  }
}