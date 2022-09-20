package bruhcollective.itaysonlab.jetispot.core.di

import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.*
import bruhcollective.itaysonlab.jetispot.core.di.ext.interceptRequest
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.core.util.create
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
  fun provideOkHttpClient(tokenHandler: ClientTokenHandler, sessionManager: SpSessionManager): OkHttpClient = OkHttpClient.Builder().apply {
    interceptRequest { orig ->
      // 1. Authorization (& client token)
      header("Authorization", "Bearer ${sessionManager.session.tokens().get("playlist-read")}")
      header("client-token", tokenHandler.requestToken())

      // 2. Default headers
      header("User-Agent", "Spotify/${SpUtils.SPOTIFY_APP_VERSION} Android/32 (Pixel 4a (5G))")
      header("Spotify-App-Version", SpUtils.SPOTIFY_APP_VERSION)
      header("App-Platform", "Android")

      // 3. Default GET params
      if (orig.method == "GET" && !orig.url.host.contains("api-partner")) {
        url(orig.url.newBuilder().apply {
          addQueryParameter("client-timezone", TimeZone.getDefault().id)
          if (!orig.url.pathSegments.contains("content-filter")) {
            addQueryParameter("platform", "android")
            addQueryParameter("locale", sessionManager.session.preferredLocale())
            addQueryParameter("video", "true")
            addQueryParameter("podcast", "true")
            addQueryParameter("application", "nft")
          }
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
  fun provideInternalApi(retrofit: Retrofit): SpInternalApi = retrofit.create("https://spclient.wg.spotify.com")

  @Provides
  @Singleton
  fun providePartnersApi(retrofit: Retrofit): SpPartnersApi = retrofit.create("https://api-partner.spotify.com")

  @Provides
  @Singleton
  fun provideCollectionApi(retrofit: Retrofit): SpCollectionApi = retrofit.create("https://spclient.wg.spotify.com/collection/v2/")

  @Provides
  @Singleton
  fun provideBlendApi(retrofit: Retrofit): SpBlendApi = retrofit.create("https://spclient.wg.spotify.com")

  @Provides
  @Singleton
  fun provideColorLyricsApi(retrofit: Retrofit): SpColorLyricsApi = retrofit.create("https://spclient.wg.spotify.com")

  @Provides
  @Singleton
  fun provideExternalIntegrationApi(retrofit: Retrofit): SpExternalIntegrationApi = retrofit.create("https://spclient.wg.spotify.com/external-integration-recs/")
}