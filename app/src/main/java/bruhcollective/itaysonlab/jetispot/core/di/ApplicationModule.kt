package bruhcollective.itaysonlab.jetispot.core.di

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.getSystemService()!!
    }
}
