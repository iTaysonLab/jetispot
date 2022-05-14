package bruhcollective.itaysonlab.jetispot.core.collection

import android.util.Log
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpCollectionApi
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionRepository
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtist
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtistMetadata
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionTrack
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import bruhcollective.itaysonlab.jetispot.core.util.Revision
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.swedentricks.protos.CollectionUpdate
import bruhcollective.itaysonlab.swedentricks.protos.CollectionUpdateEntry
import com.google.protobuf.ByteString
import com.spotify.collection2.v2.proto.Collection2V2
import com.spotify.extendedmetadata.ExtendedMetadata
import com.spotify.extendedmetadata.ExtensionKindOuterClass
import com.spotify.metadata.Metadata
import com.spotify.playlist4.Playlist4ApiProto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.AlbumId
import xyz.gianlu.librespot.metadata.ArtistId
import xyz.gianlu.librespot.metadata.ImageId
import xyz.gianlu.librespot.metadata.TrackId

class SpCollectionWriter(
  private val spSessionManager: SpSessionManager,
  private val internalApi: SpInternalApi,
  private val collectionApi: SpCollectionApi,
  private val dbRepository: LocalCollectionRepository,
  private val dao: LocalCollectionDao,
  private val scope: CoroutineScope,
): CoroutineScope by MainScope() {
  private val pendingWriteOperations = mutableListOf<CollectionWriteOp>()

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
   *
   * "listenlater" - "My Episodes"
   */
  suspend fun performScan(of: String) {
    Log.d("SpColManager", "Performing scan of $of")
    val data = performPagingScan(of)
    Log.d("SpColManager", "Scan of $of completed")
  }

  private suspend fun processPendingWrites() {
    if (pendingWriteOperations.isEmpty()) return

    collectionApi.write(Collection2V2.WriteRequest.newBuilder().apply {
      username = spSessionManager.session.username()
      set = "collection"
      clientUpdateId = SpUtils.getRandomString(16)
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

  private suspend fun performPagingScan(of: String) {
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

      Log.d(
        "SpColManager",
        "Performing page metadata request of $of [count = ${page.itemsList.size}]"
      )

      performCollectionInsert(page.itemsList.associate { Pair(it.uri, it.addedAt) })

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
  }

  private suspend fun performRootlistScan() {
    val data = internalApi.getRootlist(spSessionManager.session.username())

    val mappedData = data.contents.itemsList.mapIndexed { index, item -> Pair(item, data.contents.metaItemsList[index]) }.map { pair ->
      CollectionRootlistItem(
        uri = pair.first.uri,
        timestamp = pair.first.attributes.timestamp,
        name = pair.second.attributes.name,
        ownerUsername = pair.second.ownerUsername,
        picture = pair.second.attributes.pictureSizeList.find { it.targetName == "default" }?.url ?: ""
      )
    }.toTypedArray()

    dao.addRootListItems(*mappedData)
    dbRepository.insertOrUpdateCollection("rootlist", data.revision.toStringUtf8())
  }

  private suspend fun performCollectionInsert(mappedRequest: Map<String, Int>, mappedDeleteRequest: Map<CollectionUpdateEntry.Type, List<String>> = mapOf()) {
    Log.d("SpCollectionWriter", "collection-insert [ins -> ${mappedRequest.keys.joinToString(",")}, del -> ${mappedDeleteRequest.values.joinToString(",")}]")

    if (mappedRequest.isNotEmpty()) {
      val requests = mutableListOf<ExtendedMetadata.EntityRequest>()

      mappedRequest.keys.forEach { ci ->
        val kind = spotifyIdToKind(ci)

        if (kind != ExtensionKindOuterClass.ExtensionKind.UNKNOWN_EXTENSION) {
          requests.add(
            ExtendedMetadata.EntityRequest.newBuilder().setEntityUri(ci)
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
                ExtendedMetadata.EntityRequest.newBuilder().setEntityUri(
                  ArtistId.fromHex(Utils.bytesToHex(it.value.artistList.first().gid)).toSpotifyUri()
                ).addQuery(
                  ExtendedMetadata.ExtensionQuery.newBuilder()
                    .setExtensionKind(ExtensionKindOuterClass.ExtensionKind.ARTIST_V4).build()
                ).build()
              }.distinctBy { it.entityUri }
            ).build()
          )
          .extendedMetadataList
        ).artists.map {
          CollectionArtistMetadata(
            Utils.bytesToHex(it.value.gid).lowercase(),
            it.value.genreList.joinToString("|")
          )
        }.toTypedArray()

        dao.addMetaArtists(*mappedArtists)
      }

      dao.addTracks(*metadata.tracks.values.map { track ->
        CollectionTrack(
          id = TrackId.fromHex(Utils.bytesToHex(track.gid)).hexId(),
          uri = TrackId.fromHex(Utils.bytesToHex(track.gid)).toSpotifyUri(),
          name = track.name,
          albumId = AlbumId.fromHex(Utils.bytesToHex(track.album.gid)).hexId(),
          albumName = track.album.name,
          mainArtistId = Utils.bytesToHex(track.artistList.first().gid).lowercase(),
          rawArtistsData = track.artistList.joinToString("|") { artist -> "${ArtistId.fromHex(Utils.bytesToHex(artist.gid)).toSpotifyUri()}=${artist.name}" },
          hasLyrics = track.hasLyrics,
          isExplicit = track.explicit,
          duration = track.duration,
          picture = bytesToPicUrl(track.album.coverGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId),
          addedAt = mappedRequest[TrackId.fromHex(Utils.bytesToHex(track.gid)).toSpotifyUri()]!!
        )
      }.toTypedArray())

      dao.addAlbums(*metadata.albums.values.map { album ->
        CollectionAlbum(
          id = AlbumId.fromHex(Utils.bytesToHex(album.gid)).hexId(),
          uri = AlbumId.fromHex(Utils.bytesToHex(album.gid)).toSpotifyUri(),
          rawArtistsData = album.artistList.joinToString("|") { artist -> "${ArtistId.fromHex(Utils.bytesToHex(artist.gid)).toSpotifyUri()}=${artist.name}" },
          name = album.name,
          picture = bytesToPicUrl(album.coverGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId),
          addedAt = mappedRequest[AlbumId.fromHex(Utils.bytesToHex(album.gid)).toSpotifyUri()]!!
        )
      }.toTypedArray())

      dao.addArtists(*metadata.artists.values.map { artist ->
        CollectionArtist(
          id = ArtistId.fromHex(Utils.bytesToHex(artist.gid)).hexId(),
          uri = ArtistId.fromHex(Utils.bytesToHex(artist.gid)).toSpotifyUri(),
          name = artist.name,
          picture = bytesToPicUrl(artist.portraitGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId),
          addedAt = mappedRequest[ArtistId.fromHex(Utils.bytesToHex(artist.gid)).toSpotifyUri()]!!
        )
      }.toTypedArray())
    }

    if (mappedDeleteRequest.isNotEmpty()) {
      mappedDeleteRequest.forEach { del ->
        when (del.key) {
          CollectionUpdateEntry.Type.TRACK -> dao.deleteTracks(*del.value.toTypedArray())
          CollectionUpdateEntry.Type.ALBUM -> dao.deleteAlbums(*del.value.toTypedArray())
          CollectionUpdateEntry.Type.ARTIST -> dao.deleteArtists(*del.value.toTypedArray())
        }
      }
    }
  }

  private fun spotifyIdToKind(id: String) = when (id.split(":")[1]) {
    "track" -> ExtensionKindOuterClass.ExtensionKind.TRACK_V4
    "album" -> ExtensionKindOuterClass.ExtensionKind.ALBUM_V4
    "artist" -> ExtensionKindOuterClass.ExtensionKind.ARTIST_V4
    else -> ExtensionKindOuterClass.ExtensionKind.UNKNOWN_EXTENSION
  }

  private fun hexToSpotifyId(id: String, type: CollectionUpdateEntry.Type) = when (type) {
    CollectionUpdateEntry.Type.TRACK -> TrackId.fromHex(id).toSpotifyUri()
    CollectionUpdateEntry.Type.ALBUM -> AlbumId.fromHex(id).toSpotifyUri()
    CollectionUpdateEntry.Type.ARTIST -> ArtistId.fromHex(id).toSpotifyUri()
    CollectionUpdateEntry.Type.UNRECOGNIZED -> ""
  }

  private fun bytesToPicUrl(bytes: ByteString) = ImageId.fromHex(Utils.bytesToHex(bytes)).hexId()

  fun pubsubUpdateCollection(decoded: CollectionUpdate) = scope.launch {
    val toInsert = decoded.itemsList.filterNot { it.removed }.associate { Pair(hexToSpotifyId(Utils.bytesToHex(it.identifier), it.type), it.addedAt) }
    val toRemove = decoded.itemsList.filter { it.removed }.groupBy { it.type }.mapValues { it.value.map { li -> Utils.bytesToHex(li.identifier).lowercase() } }
    performCollectionInsert(toInsert, toRemove)
  }

  fun pubsubUpdateRootlist(decoded: Playlist4ApiProto.PlaylistModificationInfo) = scope.launch {
    if (decoded.uri.toStringUtf8() == "spotify:user:${spSessionManager.session.username()}:rootlist") {
      // perform rootlist update
      Log.d("SpColManager", "PubSub rootlist update to rev. ${Revision.base64ToRevision(decoded.newRevision.toStringUtf8())}")
    }
  }

  sealed class CollectionWriteOp(val toSet: String) {
    class Add(val spId: String, toSet: String) : CollectionWriteOp(toSet) {
      val additionDate = (System.currentTimeMillis() / 1000L).toInt()
    }

    class Remove(val spId: String, toSet: String) : CollectionWriteOp(toSet)
  }
}