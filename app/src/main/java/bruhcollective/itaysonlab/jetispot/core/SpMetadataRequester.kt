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
  // TODO: wire this up to some sort of DB (probably LevelDB like Spotify does)

  suspend fun request(uris: List<String>) = withContext(Dispatchers.IO) {
    val result = UnpackedMetadataResponse(emptyList())
    if (uris.isEmpty()) return@withContext result

    uris.chunked(400).forEach { chunkedUris ->
      result += requestImpl(chunkedUris)
    }

    return@withContext result
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
      ).extendedMetadataList)
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