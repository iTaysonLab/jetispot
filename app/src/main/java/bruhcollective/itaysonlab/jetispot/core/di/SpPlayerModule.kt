package bruhcollective.itaysonlab.jetispot.core.di

import android.content.Context
import bruhcollective.itaysonlab.jetispot.core.SpPlayerManager
import bruhcollective.itaysonlab.jetispot.playback.service.AudioFocusManager
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SpPlayerModule {
  fun provideAudioFocusManager(
    @ApplicationContext context: Context,
    spPlayerManager: SpPlayerManager
  ) = AudioFocusManager(context, spPlayerManager)
}