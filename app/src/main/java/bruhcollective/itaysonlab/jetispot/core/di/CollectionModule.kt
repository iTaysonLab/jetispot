package bruhcollective.itaysonlab.jetispot.core.di

import android.content.Context
import androidx.room.Room
import bruhcollective.itaysonlab.jetispot.core.SpMetadataRequester
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpCollectionApi
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDatabase
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CollectionModule {
  @Provides
  fun provideDatabase (
    @ApplicationContext appCtx: Context
  ): LocalCollectionDatabase = Room.databaseBuilder(appCtx, LocalCollectionDatabase::class.java, "spCollection").build()

  @Provides
  fun provideDao (
    db: LocalCollectionDatabase
  ): LocalCollectionDao = db.dao()

  fun provideRepository (
    db: LocalCollectionDatabase,
    dao: LocalCollectionDao
  ): LocalCollectionRepository = LocalCollectionRepository(db, dao)

  fun provideManager (
    spSessionManager: SpSessionManager,
    internalApi: SpInternalApi,
    collectionApi: SpCollectionApi,
    repository: LocalCollectionRepository,
    dao: LocalCollectionDao,
    metadataRequester: SpMetadataRequester
  ): SpCollectionManager = SpCollectionManager(spSessionManager, internalApi, collectionApi, repository, dao, metadataRequester)
}