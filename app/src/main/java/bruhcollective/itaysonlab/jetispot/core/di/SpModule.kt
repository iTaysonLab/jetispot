package bruhcollective.itaysonlab.jetispot.core.di

import android.content.Context
import bruhcollective.itaysonlab.jetispot.core.*
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.core.metadata_db.SpMetadataDb
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SpModule {
  fun provideSpSessionManager(
    @ApplicationContext context: Context
  ) = SpSessionManager(context)

  fun provideSpPlayerManager(
    spSessionManager: SpSessionManager,
    spConfigurationManager: SpConfigurationManager
  ) = SpPlayerManager(spSessionManager, spConfigurationManager)

  fun provideSpConfigurationManager(
    @ApplicationContext context: Context
  ) = SpConfigurationManager(context)

  fun provideSpPlayerServiceManager(
    @ApplicationContext context: Context,
    moshi: Moshi,
    sessionManager: SpSessionManager,
    metadataRequester: SpMetadataRequester
  ) = SpPlayerServiceManager(context, moshi, sessionManager, metadataRequester)

  fun provideSpAuthManager(
    spSessionManager: SpSessionManager,
    spPlayerManager: SpPlayerManager,
    spCollectionManager: SpCollectionManager
  ) = SpAuthManager(spSessionManager, spPlayerManager, spCollectionManager)

  fun provideSpMetadataDb(
    @ApplicationContext context: Context,
  ) = SpMetadataDb(context)
}