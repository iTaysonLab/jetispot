package bruhcollective.itaysonlab.jetispot.core.collection.db

import androidx.room.*
import bruhcollective.itaysonlab.jetispot.core.collection.db.model.LocalCollectionCategory
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtist
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtistMetadata
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionTrack
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem

@Dao
interface LocalCollectionDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun updateCollectionCategory(item: LocalCollectionCategory)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addTracks(vararg items: CollectionTrack)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addArtists(vararg items: CollectionArtist)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addMetaArtists(vararg items: CollectionArtistMetadata)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addAlbums(vararg items: CollectionAlbum)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addRootListItems(vararg items: CollectionRootlistItem)

  @Query("DELETE FROM lcTracks WHERE id IN (:ids)")
  suspend fun deleteTracks(vararg ids: String)

  @Query("DELETE FROM lcArtists WHERE id IN (:ids)")
  suspend fun deleteArtists(vararg ids: String)

  @Query("DELETE FROM lcMetaArtists WHERE id IN (:ids)")
  suspend fun deleteMetaArtists(vararg ids: String)

  @Query("DELETE FROM lcAlbums WHERE id IN (:ids)")
  suspend fun deleteAlbums(vararg ids: String)

  @Query("SELECT * from lcArtists ORDER BY addedAt DESC")
  suspend fun getArtists(): List<CollectionArtist>

  @Query("SELECT * from lcAlbums ORDER BY addedAt DESC")
  suspend fun getAlbums(): List<CollectionAlbum>

  @Query("SELECT * from lcTypes WHERE type = :of")
  suspend fun getCollection(of: String): LocalCollectionCategory?

  @Query("DELETE from lcTracks")
  suspend fun deleteTracks()

  @Query("DELETE from lcArtists")
  suspend fun deleteArtists()

  @Query("DELETE from lcMetaArtists")
  suspend fun deleteMetaArtists()

  @Query("DELETE from lcAlbums")
  suspend fun deleteAlbums()

  @Query("DELETE from rootlist")
  suspend fun deleteRootList()
}