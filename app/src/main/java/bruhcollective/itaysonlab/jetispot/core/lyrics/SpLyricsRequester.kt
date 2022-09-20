package bruhcollective.itaysonlab.jetispot.core.lyrics

import bruhcollective.itaysonlab.jetispot.core.api.SpColorLyricsApi
import bruhcollective.itaysonlab.jetispot.core.metadata_db.SpMetadataDb
import com.spotify.lyrics.v2.lyrics.proto.ColorLyricsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpLyricsRequester @Inject constructor(
  private val spMetadataDb: SpMetadataDb,
  private val spColorLyricsApi: SpColorLyricsApi
) {
  suspend fun request(track: String) = withContext(Dispatchers.IO) {
    val uri = "lyrics:$track"

    if (spMetadataDb.contains(uri)) {
      return@withContext ColorLyricsResponse.parseFrom(spMetadataDb.get(uri))
    } else {
      val response = spColorLyricsApi.getLyrics(spotifyId = track)

      if (response.hasLyrics()) {
        spMetadataDb.put(uri, response.toByteArray())
        return@withContext response
      } else {
        return@withContext null
      }
    }
  }
}