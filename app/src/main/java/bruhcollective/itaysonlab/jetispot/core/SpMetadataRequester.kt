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
  suspend fun request(uris: List<String>) = withContext(Dispatchers.IO) {
    val result = UnpackedMetadataResponse(emptyList())
    if (uris.isEmpty()) return@withContext result

    val dbResult = uris.partition { uri -> spMetadataDb.contains(uri) }.also { Log.d("SpApp", "in db: ${it.first.size}, to get: ${it.second.size}") }
    result += requestCachedImpl(dbResult.first.groupBy { uri -> spotifyIdToKind(uri) })
    dbResult.second.chunked(400).forEach { chunkedUris -> result += requestImpl(chunkedUris) }

    return@withContext result
  }

  private fun requestCachedImpl(uris: Map<ExtensionKindOuterClass.ExtensionKind, List<String>>): UnpackedMetadataResponse {
    return uris.map { entry ->
      ExtendedMetadata.EntityExtensionDataArray.newBuilder().setExtensionKind(entry.key).addAllExtensionData(entry.value.map { uri ->
        EntityExtensionDataOuterClass.EntityExtensionData.newBuilder().setEntityUri(uri).setExtensionData(
          Any.newBuilder().setValue(ByteString.copyFrom(spMetadataDb.get(uri))).build()
        ).build()
      }).build()
    }.let { UnpackedMetadataResponse(it) }
  }

  private fun saveUris(data: List<ExtendedMetadata.EntityExtensionDataArray>) {
    data.map { it.extensionDataList }.flatten().forEach { toSave ->
      spMetadataDb.put(toSave.entityUri, toSave.extensionData.value.toByteArray())
    }
  }

  private fun requestImpl(chunkedUris: List<String>): UnpackedMetadataResponse {
    return try {
      UnpackedMetadataResponse(spSessionManager.session.api().getExtendedMetadata(
        ExtendedMetadata.BatchedEntityRequest.newBuilder().addAllEntityRequest(
          chunkedUris.map { uri ->
            ExtendedMetadata.EntityRequest.newBuilder().setEntityUri(uri).addQuery(
              ExtendedMetadata.ExtensionQuery.newBuilder().setExtensionKind(spotifyIdToKind(uri))
                .build()
            ).build()
          }.distinctBy { it.entityUri }
        ).build()
      ).extendedMetadataList.also { saveUris(it) })
    } catch (e: Exception) {
      e.printStackTrace()
      UnpackedMetadataResponse(emptyList())
    }
  }

  private fun spotifyIdToKind(id: String) = when (id.split(":")[1]) {
    "track" -> ExtensionKindOuterClass.ExtensionKind.TRACK_V4
    "album" -> ExtensionKindOuterClass.ExtensionKind.ALBUM_V4
    "artist" -> ExtensionKindOuterClass.ExtensionKind.ARTIST_V4
    "user" -> ExtensionKindOuterClass.ExtensionKind.USER_PROFILE
    else -> ExtensionKindOuterClass.ExtensionKind.UNKNOWN_EXTENSION
  }
}