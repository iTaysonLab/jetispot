package bruhcollective.itaysonlab.jetispot.core

import bruhcollective.itaysonlab.jetispot.core.collection.UnpackedMetadataResponse
import bruhcollective.itaysonlab.jetispot.core.util.Log
import com.spotify.extendedmetadata.ExtendedMetadata
import com.spotify.extendedmetadata.ExtensionKindOuterClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpMetadataRequester @Inject constructor(
  private val spSessionManager: SpSessionManager
) {
  suspend fun request(uris: List<String>) = withContext(Dispatchers.IO) {
    val result = UnpackedMetadataResponse(emptyList())

    uris.chunked(400).forEach { chunkedUris ->
      result += UnpackedMetadataResponse(spSessionManager.session.api()
        .getExtendedMetadata(
          ExtendedMetadata.BatchedEntityRequest.newBuilder().addAllEntityRequest(
            chunkedUris.map { uri ->
              ExtendedMetadata.EntityRequest.newBuilder().setEntityUri(uri).addQuery(
                ExtendedMetadata.ExtensionQuery.newBuilder().setExtensionKind(spotifyIdToKind(uri)).build()
              ).build()
            }.distinctBy { it.entityUri }
          ).build()
        )
        .extendedMetadataList
      )
    }

    Log.d("Processed", "${result.tracks} items")

    return@withContext result
  }

  private fun spotifyIdToKind(id: String) = when (id.split(":")[1]) {
    "track" -> ExtensionKindOuterClass.ExtensionKind.TRACK_V4
    "album" -> ExtensionKindOuterClass.ExtensionKind.ALBUM_V4
    "artist" -> ExtensionKindOuterClass.ExtensionKind.ARTIST_V4
    "user" -> ExtensionKindOuterClass.ExtensionKind.USER_PROFILE
    else -> ExtensionKindOuterClass.ExtensionKind.UNKNOWN_EXTENSION
  }
}