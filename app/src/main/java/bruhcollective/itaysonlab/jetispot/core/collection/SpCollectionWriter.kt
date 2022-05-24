package bruhcollective.itaysonlab.jetispot.core.collection

import bruhcollective.itaysonlab.jetispot.core.util.Log
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpCollectionApi
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionRepository
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.*
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import bruhcollective.itaysonlab.jetispot.core.objs.tags.ContentFilterResponse
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
import xyz.gianlu.librespot.metadata.*

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
    if (of == "ylpin") {
      performPinScan(dao.getCollection(of)?.syncToken)
    } else {
      performPagingScan(of, dao.getCollection(of)?.syncToken)
    }
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

  private suspend fun performPinScan(existingSyncToken: String? = null) {
    val syncToken: String

    if (!existingSyncToken.isNullOrEmpty()) {
      val page = collectionApi.delta(Collection2V2.DeltaRequest.newBuilder().apply {
        username = spSessionManager.session.username()
        set = "ylpin"
        lastSyncToken = existingSyncToken
      }.build())

      Log.d("SpColManager", "Performing DELTA page metadata request of pins [count = ${page.itemsList.size}]")

      val toAdd = page.itemsList.filterNot { it.isRemoved }.associate { Pair(it.uri, it.addedAt) }
      val toRemove = page.itemsList.filter { it.isRemoved }.map { it.uri }

      performPinInsert(toAdd, toRemove)
      syncToken = page.syncToken
    } else {
      Log.d("SpColManager", "Performing page request of pins")

      val page = collectionApi.paging(Collection2V2.PageRequest.newBuilder().apply {
        username = spSessionManager.session.username()
        set = "ylpin"
        paginationToken = ""
        limit = 300
      }.build())

      Log.d(
        "SpColManager",
        "Performing page metadata request of pins [count = ${page.itemsList.size}]"
      )

      performPinInsert(page.itemsList.associate { Pair(it.uri, it.addedAt) })
      syncToken = page.syncToken
    }

    dbRepository.insertOrUpdateCollection("ylpin", syncToken)
  }

  private suspend fun performPagingScan(of: String, existingSyncToken: String? = null) {
    val syncToken: String
    var pToken = ""

    if (!existingSyncToken.isNullOrEmpty()) {
      val page = collectionApi.delta(Collection2V2.DeltaRequest.newBuilder().apply {
        username = spSessionManager.session.username()
        set = of
        lastSyncToken = existingSyncToken
      }.build())

      Log.d("SpColManager", "Performing DELTA page metadata request of $of [count = ${page.itemsList.size}]")

      val toAdd = page.itemsList.filterNot { it.isRemoved }.associate { Pair(it.uri, it.addedAt) }
      val toRemove = page.itemsList.filter { it.isRemoved }.groupBy { when (spotifyIdToKind(it.uri)) {
        ExtensionKindOuterClass.ExtensionKind.ARTIST_V4 -> CollectionUpdateEntry.Type.ARTIST
        ExtensionKindOuterClass.ExtensionKind.ALBUM_V4 -> CollectionUpdateEntry.Type.ALBUM
        ExtensionKindOuterClass.ExtensionKind.TRACK_V4 -> CollectionUpdateEntry.Type.TRACK
        else -> error("unsupported")
      }}.mapValues { it.value.map { li -> li.uri } }

      performCollectionInsert(toAdd, toRemove)
      syncToken = page.syncToken
    } else {
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
    }

    dbRepository.insertOrUpdateCollection(of, syncToken)
  }

  suspend fun performContentFiltersScan() {
    val data = try { internalApi.getCollectionTags() } catch (e: Exception) { ContentFilterResponse(emptyList()) }

    val mappedData = data.contentFilters.map {
      CollectionContentFilter(name = it.title, query = it.query)
    }.toTypedArray()

    dao.deleteContentFilters()
    dao.addContentFilters(*mappedData)
  }

  private suspend fun performPinInsert(mappedRequest: Map<String, Int>, delete: List<String> = emptyList()) {
    Log.d("SpCollectionWriter", "pin-insert [ins -> ${mappedRequest.keys.joinToString(",")}, del -> ${delete.joinToString(",")}]")

    if (mappedRequest.isNotEmpty()) {
      val metadata = getExtendedMetadata(mappedRequest.keys.filterNot { it.contains("playlist") || it.contains("collection") })
      val playlistItems = mappedRequest.filterKeys { it.contains("playlist") }
      val collectionItems = mappedRequest.filterKeys { it.contains("collection") }
      val toBeAdded = mutableListOf<CollectionPinnedItem>()

      toBeAdded.addAll(metadata.albums.values.map { album ->
        CollectionPinnedItem(
          uri = AlbumId.fromHex(Utils.bytesToHex(album.gid)).toSpotifyUri(),
          name = album.name,
          subtitle = album.artistList.joinToString { artist -> artist.name },
          picture = bytesToPicUrl(album.coverGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId),
          addedAt = mappedRequest[AlbumId.fromHex(Utils.bytesToHex(album.gid)).toSpotifyUri()]!!
        )
      })

      toBeAdded.addAll(metadata.artists.values.map { artist ->
        CollectionPinnedItem(
          uri = ArtistId.fromHex(Utils.bytesToHex(artist.gid)).toSpotifyUri(),
          name = artist.name,
          subtitle = "",
          picture = bytesToPicUrl(artist.portraitGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId),
          addedAt = mappedRequest[ArtistId.fromHex(Utils.bytesToHex(artist.gid)).toSpotifyUri()]!!
        )
      })

      toBeAdded.addAll(playlistItems.map {
        Triple(it.key, it.value, spSessionManager.session.api().getPlaylist(PlaylistId.fromUri(it.key)))
      }.map { playlist ->
        CollectionPinnedItem(
          uri = playlist.first,
          name = playlist.third.attributes.name,
          subtitle = playlist.third.ownerUsername,
          picture = Utils.bytesToHex(playlist.third.attributes.picture).lowercase(),
          addedAt = playlist.second
        )
      })

      toBeAdded.addAll(collectionItems.map { ci ->
        CollectionPinnedItem(
          uri = ci.key,
          name = "",
          subtitle = "",
          picture = "",
          addedAt = ci.value
        )
      })

      dao.addPins(*toBeAdded.toTypedArray())
    }

    if (delete.isNotEmpty()) {
      dao.deletePins(*delete.toTypedArray())
    }
  }

  private suspend fun performCollectionInsert(mappedRequest: Map<String, Int>, mappedDeleteRequest: Map<CollectionUpdateEntry.Type, List<String>> = mapOf()) {
    Log.d("SpCollectionWriter", "collection-insert [ins -> ${mappedRequest.keys.joinToString(",")}, del -> ${mappedDeleteRequest.values.joinToString(",")}]")

    if (mappedRequest.isNotEmpty()) {
      val metadata = getExtendedMetadata(mappedRequest.keys.toList())

      val trackDescriptors = if (metadata.tracks.isNotEmpty()) {
        // also request descriptors to get genre data
        UnpackedMetadataResponse(spSessionManager.session.api()
          .getExtendedMetadata(
            ExtendedMetadata.BatchedEntityRequest.newBuilder().addAllEntityRequest(
              metadata.tracks.keys.map { ExtendedMetadata.EntityRequest.newBuilder().setEntityUri(it
                ).addQuery(
                  ExtendedMetadata.ExtensionQuery.newBuilder().setExtensionKind(ExtensionKindOuterClass.ExtensionKind.TRACK_DESCRIPTOR).build()
                ).build()
              }.distinctBy { it.entityUri }
            ).build()
          )
          .extendedMetadataList
        ).descriptors.mapValues { e ->
          e.value.descriptorsList.joinToString("|") { it.text }
        }
      } else {
        emptyMap()
      }

      dao.addTracks(*metadata.tracks.values.map { track ->
        val spUri = TrackId.fromHex(Utils.bytesToHex(track.gid)).toSpotifyUri()
        CollectionTrack(
          id = TrackId.fromHex(Utils.bytesToHex(track.gid)).hexId(),
          uri = spUri,
          name = track.name,
          albumId = AlbumId.fromHex(Utils.bytesToHex(track.album.gid)).hexId(),
          albumName = track.album.name,
          mainArtistName = track.artistList.first().name,
          mainArtistId = Utils.bytesToHex(track.artistList.first().gid).lowercase(),
          rawArtistsData = track.artistList.joinToString("|") { artist -> "${ArtistId.fromHex(Utils.bytesToHex(artist.gid)).toSpotifyUri()}=${artist.name}" },
          hasLyrics = track.hasLyrics,
          isExplicit = track.explicit,
          duration = track.duration,
          picture = bytesToPicUrl(track.album.coverGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId),
          descriptors = trackDescriptors[spUri] ?: "",
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

  private suspend fun getExtendedMetadata(of: List<String>): UnpackedMetadataResponse {
    if (of.isEmpty()) return UnpackedMetadataResponse(emptyList())

    val requests = mutableListOf<ExtendedMetadata.EntityRequest>()

    of.forEach { ci ->
      val kind = spotifyIdToKind(ci)

      if (kind != ExtensionKindOuterClass.ExtensionKind.UNKNOWN_EXTENSION) {
        requests.add(
          ExtendedMetadata.EntityRequest.newBuilder().setEntityUri(ci)
            .addQuery(ExtendedMetadata.ExtensionQuery.newBuilder().setExtensionKind(kind).build())
            .build()
        )
      }
    }

    return UnpackedMetadataResponse(
      spSessionManager.session.api().getExtendedMetadata(
        ExtendedMetadata.BatchedEntityRequest.newBuilder().addAllEntityRequest(requests).build()
      ).extendedMetadataList
    )
  }

  suspend fun performRootlistScan(updateToRevision: String? = null) {
    val localRevision = dao.getCollection("rootlist")?.syncToken
    Log.d("SpCollectionWriter", "rootlist-scan [$localRevision -> $updateToRevision]")

    if (localRevision != null) {
      val delta = internalApi.getRootlistDelta(spSessionManager.session.username(), revision = localRevision, targetRevision = updateToRevision ?: "")

      delta.diff.opsList.forEach { op ->
        Log.d("SpCollectionWriter", "rootlist-scan/delta op [${op.kind}]")
        when (op.kind) {
          Playlist4ApiProto.Op.Kind.ADD -> {
            dao.addRootListItems(*op.add.itemsList.map { Pair(it, spSessionManager.session.api().getPlaylist(PlaylistId.fromUri(it.uri))) }.map { pair ->
              CollectionRootlistItem(
                uri = pair.first.uri,
                timestamp = pair.first.attributes.timestamp,
                name = pair.second.attributes.name,
                ownerUsername = pair.second.ownerUsername,
                picture = pair.second.attributes.pictureSizeList.find { it.targetName == "default" }?.url ?: "https://i.scdn.co/image/${Utils.bytesToHex(pair.second.attributes.picture).lowercase()}"
              )
            }.toTypedArray())
          }
          Playlist4ApiProto.Op.Kind.REM -> {
            dao.deleteRootList(*op.rem.itemsList.map { it.uri }.toTypedArray())
          }
          Playlist4ApiProto.Op.Kind.UPDATE_ITEM_ATTRIBUTES -> {}
          Playlist4ApiProto.Op.Kind.UPDATE_LIST_ATTRIBUTES -> {}
          Playlist4ApiProto.Op.Kind.MOV -> {} // TODO
          null, Playlist4ApiProto.Op.Kind.KIND_UNKNOWN -> {}
        }
      }

      dbRepository.insertOrUpdateCollection("rootlist", Revision.byteStringToRevision(delta.diff.toRevision))
      return
    }

    val data = internalApi.getRootlist(spSessionManager.session.username())

    val mappedData = data.contents.itemsList.mapIndexed { index, item -> Pair(item, data.contents.metaItemsList[index]) }.map { pair ->
      CollectionRootlistItem(
        uri = pair.first.uri,
        timestamp = pair.first.attributes.timestamp,
        name = pair.second.attributes.name,
        ownerUsername = pair.second.ownerUsername,
        picture = pair.second.attributes.pictureSizeList.find { it.targetName == "default" }?.url ?: "https://i.scdn.co/image/${Utils.bytesToHex(pair.second.attributes.picture).lowercase()}"
      )
    }.toTypedArray()

    dao.addRootListItems(*mappedData)
    Log.d("SpCollectionWriter", "rootlist-insert done [rev -> ${Revision.byteStringToRevision(data.revision)}]")
    dbRepository.insertOrUpdateCollection("rootlist", Revision.byteStringToRevision(data.revision))
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
      Log.d("SpColManager", "PubSub rootlist update to rev. ${Revision.byteStringToRevision(decoded.newRevision)}")
      performRootlistScan(updateToRevision = Revision.byteStringToRevision(decoded.newRevision))
    }
  }

  sealed class CollectionWriteOp(val toSet: String) {
    class Add(val spId: String, toSet: String) : CollectionWriteOp(toSet) {
      val additionDate = (System.currentTimeMillis() / 1000L).toInt()
    }

    class Remove(val spId: String, toSet: String) : CollectionWriteOp(toSet)
  }
}