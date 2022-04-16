package bruhcollective.itaysonlab.jetispot.core

import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.Response
import okio.IOException
import okio.use
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("BlockingMethodInNonBlockingContext", "OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalStdlibApi::class)
@Singleton
class SpApiManager @Inject constructor(
  private val sessionManager: SpSessionManager,
  private val moshi: Moshi
) {
  suspend fun getHomeView() = getJson<HubResponse>("/homeview/v1/home?platform=android&client-timezone=${TimeZone.getDefault().id}&locale=${sessionManager.session.preferredLocale()}&video=true&podcast=true&is_car_connected=false")
  suspend fun getBrowseView(pageId: String = "") = getJson<HubResponse>("/hubview-mobile-v1/browse/$pageId?platform=android&client-timezone=${TimeZone.getDefault().id}&locale=${sessionManager.session.preferredLocale()}&podcast=true")
  suspend fun getAlbumView(id: String) = getJson<HubResponse>("/album-entity-view/v2/album/$id?platform=android&application=nft&checkDeviceCapability=true&locale=${sessionManager.session.preferredLocale()}&video=true&podcast=true")
  suspend fun getArtistView(id: String) = getJson<HubResponse>("/artistview/v1/artist/$id?platform=android&application=nft&checkDeviceCapability=true&client-timezone=${TimeZone.getDefault().id}&purchase_allowed=false&timeFormat=24h&locale=${sessionManager.session.preferredLocale()}&video=true&podcast=true")

  private suspend inline fun <reified T> getJson(suffix: String) = get<T>(suffix) { res ->
    res.body!!.source().use {
      moshi.adapter<T>().fromJson(it)!!
    }
  }

  private suspend fun <T> get(suffix: String, usageBlock: (Response) -> T) = withContext(Dispatchers.IO) {
    sessionManager.session.api().send(
      "GET", suffix, Headers.headersOf("User-Agent", "Spotify/8.7.20.1261 Android/32 (Pixel 4a (5G))", "Spotify-App-Version", "8.7.20.1261"), null
    ).use { resp ->
      if (resp.code != 200) throw StatusCodeException(resp)
      usageBlock(resp)
    }
  }

  class StatusCodeException(response: Response): IOException("${response.code}: ${response.message}")
}