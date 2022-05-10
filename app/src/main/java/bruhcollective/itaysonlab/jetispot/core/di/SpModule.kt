package bruhcollective.itaysonlab.jetispot.core.di

import android.content.Context
import bruhcollective.itaysonlab.jetispot.core.*
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
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
    moshi: Moshi
  ) = SpPlayerServiceManager(context, moshi)

  fun provideSpAuthManager(
    spSessionManager: SpSessionManager,
    spPlayerManager: SpPlayerManager,
    spCollectionManager: SpCollectionManager
  ) = SpAuthManager(spSessionManager, spPlayerManager, spCollectionManager)
}