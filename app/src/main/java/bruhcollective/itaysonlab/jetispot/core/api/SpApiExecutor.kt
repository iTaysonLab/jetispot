package bruhcollective.itaysonlab.jetispot.core.api

import bruhcollective.itaysonlab.jetispot.core.DeviceIdProvider
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Message
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.BufferedSource
import okio.IOException
import okio.use
import java.io.InputStream
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
  suspend inline fun <reified T: Message> getProto(edge: Edge = Edge.Internal, suffix: String, params: Map<String, String>) = get(edge, suffix, params) { res -> parseReflect<T>(res) }
  suspend inline fun <reified In : Message, reified Out : Message> postProto(edge: Edge = Edge.Internal, suffix: String, body: In) = request<Out>(edge, "POST", suffix, body.toByteArray().toRequestBody("application/protobuf".toMediaType())) { res -> parseReflect(res) }

  suspend inline fun <reified T> getJson(edge: Edge = Edge.Internal, suffix: String, params: Map<String, String>) = get<T>(edge, suffix, params) { res ->
    res.body!!.source().use {
      moshi.adapter<T>().fromJson(it)!!
    }
  }

  inline fun <reified T: Message> parseReflect(response: Response) = response.body!!.source().inputStream().use {
    T::class.java.getDeclaredMethod("parseFrom", InputStream::class.java).invoke(null, it) as T
  }

  suspend fun <T> get(edge: Edge = Edge.Internal, suffix: String, params: Map<String, String>, usageBlock: (Response) -> T) = request(edge, "GET", "$suffix?" + (params + if (edge == Edge.Internal) arrayOf(
    "platform" to "android",
    "client-timezone" to TimeZone.getDefault().id,
    "locale" to sessionManager.session.preferredLocale(),
    "video" to "true",
    "podcast" to "true",
    "application" to "nft",
  ) else arrayOf()).map { "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}" }.joinToString("&"), null, usageBlock)

  suspend fun <T> request(edge: Edge = Edge.Internal, type: String, url: String, body: RequestBody?, usageBlock: (Response) -> T) = withContext(Dispatchers.IO) {
    val headers = when (edge) {
      Edge.Internal -> Headers.headersOf("User-Agent", "Spotify/${DeviceIdProvider.SPOTIFY_APP_VERSION} Android/32 (Pixel 4a (5G))", "Spotify-App-Version", DeviceIdProvider.SPOTIFY_APP_VERSION, "App-Platform", "Android")
      else -> Headers.headersOf("User-Agent", "Spotify/${DeviceIdProvider.SPOTIFY_APP_VERSION} Android/32 (Pixel 4a (5G))", "Spotify-App-Version", DeviceIdProvider.SPOTIFY_APP_VERSION, "App-Platform", "Android", "Authorization", "Bearer ${sessionManager.session.tokens().get("playlist-read")}")
    }

    when (edge) {
      is Edge.Internal -> sessionManager.session.api().send(type, url, headers, body)
      else -> sessionManager.session.client().newCall(Request.Builder().method(type, body).url("https://${edge.domain}$url").headers(headers).build()).execute()
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