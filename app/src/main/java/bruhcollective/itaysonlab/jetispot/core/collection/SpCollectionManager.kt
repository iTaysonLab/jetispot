package bruhcollective.itaysonlab.jetispot.core.collection

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import bruhcollection.itaysonlab.swedenmindbreaks.PlaylistHmRes
import bruhcollective.itaysonlab.jetispot.core.DeviceIdProvider
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpCollectionApi
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionRepository
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtist
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtistMetadata
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionTrack
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import com.google.protobuf.ByteString
import com.spotify.collection2.v2.proto.Collection2V2
import com.spotify.extendedmetadata.ExtendedMetadata
import com.spotify.extendedmetadata.ExtensionKindOuterClass
import com.spotify.metadata.Metadata
import com.spotify.playlist4.Playlist4ApiProto
import kotlinx.coroutines.*
import xyz.gianlu.librespot.common.Base62
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.dealer.DealerClient
import xyz.gianlu.librespot.metadata.*
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

class SpCollectionManager @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val internalApi: SpInternalApi,
  private val collectionApi: SpCollectionApi,
  private val dbRepository: LocalCollectionRepository
): DealerClient.MessageListener {
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

  private suspend fun performRootlistScan() {
    val data = internalApi.getRootlist(spSessionManager.session.username())

    val mappedData = data.contents.itemsList.mapIndexed { index, item -> Pair(item, data.contents.metaItemsList[index]) }.map { pair ->
      CollectionRootlistItem(
        uri = pair.first.uri,
        timestamp = pair.first.attributes.timestamp,
        name = pair.second.attributes.name,
        ownerUsername = pair.second.ownerUsername,
        picture = pair.second.attributes.unknownFields.getField(13)?.lengthDelimitedList
          ?.get(0)?.toStringUtf8()
          ?.split(Regex(".default.."))?.get(1) ?: ""
      )
    }.toTypedArray()

    dbRepository.insertRootList(*mappedData)
    dbRepository.insertOrUpdateCollection("rootlist", data.revision.toStringUtf8())
  }

  fun init() {
    //spSessionManager.session.dealer().addMessageListener(this, "hm://collection/collection/" + spSessionManager.session.username())
    //spSessionManager.session.dealer().addMessageListener(this, "hm://collection/artist/" + spSessionManager.session.username())
    //spSessionManager.session.dealer().addMessageListener(this, "hm://playlist/v2/user/${spSessionManager.session.username()}/rootlist")
  }

  override fun onMessage(p0: String, p1: MutableMap<String, String>, p2: ByteArray) {
    Log.d("SpColManager", "<onMessage: $p0 / $p1> = ${p2.decodeToString()} / ${Utils.bytesToHex(p2)}")
    if (p0.startsWith("hm://playlist/v2/user/")) {
      val decoded = Playlist4ApiProto.PlaylistModificationInfo.parseFrom(p2)
      if (decoded.uri.toStringUtf8() == "spotify:user:${spSessionManager.session.username()}:rootlist") {
        // perform rootlist update
      }
    } else {

    }
  }
}