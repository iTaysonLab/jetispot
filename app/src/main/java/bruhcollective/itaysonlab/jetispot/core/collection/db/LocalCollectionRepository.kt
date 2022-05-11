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

  suspend fun insertMetaArtists(vararg items: CollectionArtistMetadata) = dao.addMetaArtists(*items)
  suspend fun insertArtists(vararg items: CollectionArtist) = dao.addArtists(*items)
  suspend fun insertAlbums(vararg items: CollectionAlbum) = dao.addAlbums(*items)
  suspend fun insertTracks(vararg items: CollectionTrack) = dao.addTracks(*items)
  suspend fun insertRootList(vararg items: CollectionRootlistItem) = dao.addRootListItems(*items)

  suspend fun getArtists() = dao.getArtists()
  suspend fun getAlbums() = dao.getAlbums()
  suspend fun getCollection(of: String): LocalCollectionCategory? = dao.getCollection(of)

  suspend fun deleteTracks(vararg ids: String) = dao.deleteTracks(*ids)
  suspend fun deleteAlbums(vararg ids: String) = dao.deleteAlbums(*ids)
  suspend fun deleteArtists(vararg ids: String) = dao.deleteArtists(*ids)

  suspend fun clean() {
    dao.deleteTracks()
    dao.deleteAlbums()
    dao.deleteArtists()
    dao.deleteMetaArtists()
    dao.deleteRootList()
  }
}