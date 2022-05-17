package bruhcollective.itaysonlab.jetispot.core.collection.db

import bruhcollective.itaysonlab.jetispot.core.collection.db.model.LocalCollectionCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalCollectionRepository @Inject constructor(
  private val db: LocalCollectionDatabase,
  private val dao: LocalCollectionDao
) {
  suspend fun insertOrUpdateCollection(
    collectionSet: String,
    syncToken: String
  ) {
    dao.updateCollectionCategory(LocalCollectionCategory(collectionSet, syncToken))
  }

  suspend fun clean() {
    withContext(Dispatchers.Default) { db.clearAllTables() }
  }
}