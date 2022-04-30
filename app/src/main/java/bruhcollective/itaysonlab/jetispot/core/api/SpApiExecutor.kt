package bruhcollective.itaysonlab.jetispot.core.api

import android.util.Log
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import okio.use
import java.net.URLEncoder
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalStdlibApi::class)
@Singleton
class SpApiExecutor @Inject constructor(
  val sessionManager: SpSessionManager,
  val moshi: Moshi
) {
  suspend inline fun <reified T> getJson(edge: Edge = Edge.Internal, suffix: String, params: Map<String, String>) = get<T>(edge, suffix, params) { res ->
    res.body!!.source().use {
      moshi.adapter<T>().fromJson(it)!!
    }
  }

  suspend fun <T> get(edge: Edge = Edge.Internal, suffix: String, params: Map<String, String>, usageBlock: (Response) -> T) = withContext(Dispatchers.IO) {
    val url = "$suffix?" + (params + if (edge == Edge.Internal) arrayOf(
      "platform" to "android",
      "client-timezone" to TimeZone.getDefault().id,
      "locale" to sessionManager.session.preferredLocale(),
      "video" to "true",
      "podcast" to "true",
      "application" to "nft",
    ) else arrayOf()).map { "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}" }.joinToString("&")

    val headers = Headers.headersOf("User-Agent", "Spotify/8.7.20.1261 Android/32 (Pixel 4a (5G))", "Spotify-App-Version", "8.7.20.1261", "Authorization", "Bearer ${sessionManager.session.tokens().get("playlist-read")}")

    when (edge) {
      is Edge.Internal -> {
        sessionManager.session.api().send(
          "GET", url, headers, null
        )
      }

      else -> {
        sessionManager.session.client().newCall(Request.Builder().method("GET", null).url("https://${edge.domain}$url").headers(headers).build()).execute()
      }
    }.use { resp ->
      if (resp.code != 200) throw StatusCodeException(resp)
      usageBlock(resp)
    }
  }

  class StatusCodeException(response: Response): IOException("${response.code}: ${response.message}")

  sealed class Edge (val domain: String) {
    // spclient
    object Internal: Edge("")

    // public
    object Public: Edge("api.spotify.com")

    // partner graphql
    object Partner: Edge("api-partner.spotify.com")
  }
}