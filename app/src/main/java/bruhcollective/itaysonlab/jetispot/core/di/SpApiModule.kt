package bruhcollective.itaysonlab.jetispot.core.di

import bruhcollective.itaysonlab.jetispot.core.SpApiManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpApiExecutor
import bruhcollective.itaysonlab.jetispot.core.api.edges.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.api.edges.SpPartnersApi
import bruhcollective.itaysonlab.jetispot.core.api.edges.SpPublicApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SpApiModule {
  fun provideSpApiManager(
    internalApi: SpInternalApi,
    publicApi: SpPublicApi,
    partnersApi: SpPartnersApi
  ) = SpApiManager(internalApi, publicApi, partnersApi)

  fun provideSpApiExecutor(
    spSessionManager: SpSessionManager,
    moshi: Moshi
  ) = SpApiExecutor(spSessionManager, moshi)

  // edges

  fun provideInternalApi(
    spApiExecutor: SpApiExecutor
  ) = SpInternalApi(spApiExecutor)

  fun providePartnersApi(
    spApiExecutor: SpApiExecutor
  ) = SpPartnersApi(spApiExecutor)

  fun providePublicApi(
    spApiExecutor: SpApiExecutor
  ) = SpPublicApi(spApiExecutor)
}