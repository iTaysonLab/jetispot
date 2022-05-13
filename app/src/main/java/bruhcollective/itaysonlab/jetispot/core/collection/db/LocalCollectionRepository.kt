package bruhcollective.itaysonlab.jetispot.core.collection.db

import bruhcollective.itaysonlab.jetispot.core.collection.db.model.LocalCollectionCategory
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtist
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtistMetadata
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionTrack
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalCollectionRepository @Inject constructor(
  private val dao: LocalCollectionDao
) {
  suspend fun insertOrUpdateCollection(
    collectionSet: String,
    syncToken: String
  ) {
    dao.updateCollectionCategory(LocalCollectionCategory(collectionSet, syncToken))
  }

  suspend fun clean() {
    dao.deleteTracks()
    dao.deleteAlbums()
    dao.deleteArtists()
    dao.deleteMetaArtists()
    dao.deleteRootList()
  }
}