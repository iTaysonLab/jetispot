package bruhcollective.itaysonlab.jetispot.core

import bruhcollective.itaysonlab.jetispot.core.collection.UnpackedMetadataResponse
import bruhcollective.itaysonlab.jetispot.core.metadata_db.SpMetadataDb
import bruhcollective.itaysonlab.jetispot.core.util.Log
import com.google.protobuf.Any
import com.google.protobuf.ByteString
import com.spotify.extendedmetadata.EntityExtensionDataOuterClass
import com.spotify.extendedmetadata.ExtendedMetadata
import com.spotify.extendedmetadata.ExtensionKindOuterClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpMetadataRequester @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val spMetadataDb: SpMetadataDb
) {
  companion object {
    private val coreKinds = arrayOf(
      ExtensionKindOuterClass.ExtensionKind.EPISODE_V4,
      ExtensionKindOuterClass.ExtensionKind.SHOW_V4,
      ExtensionKindOuterClass.ExtensionKind.ARTIST_V4,
      ExtensionKindOuterClass.ExtensionKind.ALBUM_V4,
      ExtensionKindOuterClass.ExtensionKind.TRACK_V4,
      ExtensionKindOuterClass.ExtensionKind.USER_PROFILE
    )
  }

  private inline fun <K, V> MutableMap<K, V>.runOrAdd(key: K, ifExists: (V) -> Unit, ifNotExists: () -> V) {
    val value = get(key)
    if (value != null) {
      ifExists(value)
    } else {
      put(key, ifNotExists())
    }
  }

  suspend fun request(builder: MutableRequestEntities.() -> Unit): UnpackedMetadataResponse = request(buildList(builder))
  suspend fun request(entities: RequestEntities) = withContext(Dispatchers.IO) {
    val result = UnpackedMetadataResponse(emptyList())
    if (entities.isEmpty()) return@withContext result

    val alreadyCached = mutableMapOf<ExtensionKindOuterClass.ExtensionKind, ExtendedMetadata.EntityExtensionDataArray.Builder>()
    val shouldRequest = mutableMapOf<String, ExtendedMetadata.EntityRequest.Builder>()

    entities.forEach { entity ->
      val uri = entity.first
      entity.second.forEach { kind ->
        val kindInDbUri = if (coreKinds.contains(kind)) uri else "$uri:${kind.ordinal}"
        if (spMetadataDb.contains(kindInDbUri)) {
          val extensionData = EntityExtensionDataOuterClass.EntityExtensionData.newBuilder().setEntityUri(uri).setExtensionData(Any.newBuilder().setValue(ByteString.copyFrom(spMetadataDb.get(uri))).build()).build()
          alreadyCached.runOrAdd(kind, ifExists = {
            it.addExtensionData(extensionData)
          }, ifNotExists = {
            ExtendedMetadata.EntityExtensionDataArray.newBuilder().setExtensionKind(kind).addExtensionData(extensionData)
          })
        } else {
          val query = ExtendedMetadata.ExtensionQuery.newBuilder().setExtensionKind(kind).build()
          shouldRequest.runOrAdd(uri, ifExists = {
            it.addQuery(query)
          }, ifNotExists = {
            ExtendedMetadata.EntityRequest.newBuilder().setEntityUri(uri).addQuery(query)
          })
        }
      }
    }

    result += UnpackedMetadataResponse(alreadyCached.map { it.value.build() })

    shouldRequest.map { it.value.build() }.chunked(400).forEach {
      result += requestNetwork(it)
    }

    return@withContext result
  }

  private fun requestNetwork(
    queries: List<ExtendedMetadata.EntityRequest>
  ): UnpackedMetadataResponse {
    return try {
      UnpackedMetadataResponse(spSessionManager.session.api().getExtendedMetadata(
        ExtendedMetadata.BatchedEntityRequest.newBuilder().addAllEntityRequest(queries).build()
      ).extendedMetadataList.also { saveUris(it) })
    } catch (e: Exception) {
      e.printStackTrace()
      UnpackedMetadataResponse(emptyList())
    }
  }

  private fun saveUris(data: List<ExtendedMetadata.EntityExtensionDataArray>) {
    data.forEach { arr ->
      val kind = arr.extensionKind
      arr.extensionDataList.forEach { toSave ->
        spMetadataDb.put(toSave.entityUri.let { uri ->
          if (coreKinds.contains(kind)) uri else "$uri:${kind.ordinal}"
        }, toSave.extensionData.value.toByteArray())
      }
    }
  }
}

typealias MutableRequestEntities = MutableList<Pair<String, List<ExtensionKindOuterClass.ExtensionKind>>>
typealias RequestEntities = List<Pair<String, List<ExtensionKindOuterClass.ExtensionKind>>>

fun MutableRequestEntities.user(uri: String) = add(uri to listOf(ExtensionKindOuterClass.ExtensionKind.USER_PROFILE))
fun MutableRequestEntities.album(uri: String) = add(uri to listOf(ExtensionKindOuterClass.ExtensionKind.ALBUM_V4))
fun MutableRequestEntities.artist(uri: String) = add(uri to listOf(ExtensionKindOuterClass.ExtensionKind.ARTIST_V4))
fun MutableRequestEntities.track(uri: String) = add(uri to listOf(ExtensionKindOuterClass.ExtensionKind.TRACK_V4))

fun MutableRequestEntities.tracks(uris: List<String>) = addAll(uris.map { it to listOf(ExtensionKindOuterClass.ExtensionKind.TRACK_V4) })
fun MutableRequestEntities.episodes(uris: List<String>) = addAll(uris.map { it to listOf(ExtensionKindOuterClass.ExtensionKind.EPISODE_V4) })
fun MutableRequestEntities.raw(uris: List<String>) = addAll(uris.map { it to listOf(spotifyIdToKind(it)) })

private fun spotifyIdToKind(id: String) = when (id.split(":")[1]) {
  "track" -> ExtensionKindOuterClass.ExtensionKind.TRACK_V4
  "album" -> ExtensionKindOuterClass.ExtensionKind.ALBUM_V4
  "artist" -> ExtensionKindOuterClass.ExtensionKind.ARTIST_V4
  "episode" -> ExtensionKindOuterClass.ExtensionKind.EPISODE_V4
  else -> ExtensionKindOuterClass.ExtensionKind.UNKNOWN_EXTENSION
}