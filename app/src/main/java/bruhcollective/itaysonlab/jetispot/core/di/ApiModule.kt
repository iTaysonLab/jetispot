package bruhcollective.itaysonlab.jetispot.core.di

import bruhcollective.itaysonlab.jetispot.core.DeviceIdProvider
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.ClientTokenHandler
import bruhcollective.itaysonlab.jetispot.core.api.SpCollectionApi
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.di.ext.interceptRequest
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
  @Provides
  @Singleton
  fun provideMoshi(): Moshi = Moshi.Builder().build()

  @Provides
  @Singleton
  fun provideClientTokenHandler(spSessionManager: SpSessionManager) = ClientTokenHandler(spSessionManager)

  @Provides
  @Singleton
  fun provideOkHttpClient(tokenHandler: ClientTokenHandler, sessionManager: SpSessionManager): OkHttpClient = OkHttpClient.Builder().apply {
    interceptRequest { orig ->
      // 1. Authorization (& client token)
      header("Authorization", "Bearer ${sessionManager.session.tokens().get("playlist-read")}")
      header("client-token", tokenHandler.requestToken())

      // 2. Default headers
      header("User-Agent", "Spotify/${DeviceIdProvider.SPOTIFY_APP_VERSION} Android/32 (Pixel 4a (5G))")
      header("Spotify-App-Version", DeviceIdProvider.SPOTIFY_APP_VERSION)
      header("App-Platform", "Android")

      // 3. Default GET params
      if (orig.method == "GET") {
        url(orig.url.newBuilder().apply {
          addQueryParameter("platform", "android")
          addQueryParameter("client-timezone", TimeZone.getDefault().id)
          addQueryParameter("locale", sessionManager.session.preferredLocale())
          addQueryParameter("video", "true")
          addQueryParameter("podcast", "true")
          addQueryParameter("application", "nft")
        }.build())
      }
    }
  }.build()

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder().apply {
    addConverterFactory(ProtoConverterFactory.create())
    addConverterFactory(MoshiConverterFactory.create())
    baseUrl("https://spclient.wg.spotify.com")
    client(okHttpClient)
  }.build()

  @Provides
  @Singleton
  fun provideInternalApi(retrofit: Retrofit): SpInternalApi = retrofit.newBuilder().baseUrl("https://spclient.wg.spotify.com").build().create(SpInternalApi::class.java)

  @Provides
  @Singleton
  fun provideCollectionApi(retrofit: Retrofit): SpCollectionApi = retrofit.newBuilder().baseUrl("https://spclient.wg.spotify.com/collection/v2/").build().create(SpCollectionApi::class.java)
}