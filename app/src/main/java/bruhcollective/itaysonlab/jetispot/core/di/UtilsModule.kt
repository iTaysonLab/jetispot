package bruhcollective.itaysonlab.jetispot.core.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {
  @Provides
  @Singleton
  fun provideMoshi(): Moshi = Moshi.Builder().build()
}