package bruhcollective.itaysonlab.jetispot.core.collection.db

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import bruhcollective.itaysonlab.jetispot.core.collection.db.model.LocalCollectionCategory
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.*
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalCollectionDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun updateCollectionCategory(item: LocalCollectionCategory)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addTracks(vararg items: CollectionTrack)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addArtists(vararg items: CollectionArtist)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addAlbums(vararg items: CollectionAlbum)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addRootListItems(vararg items: CollectionRootlistItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addContentFilters(vararg items: CollectionContentFilter)

  @Query("SELECT * from lcTypes WHERE type = :of")
  suspend fun getCollection(of: String): LocalCollectionCategory?

  @Query("SELECT * from lcFilters")
  suspend fun getContentFilters(): List<CollectionContentFilter>

  @Query("DELETE FROM lcTracks WHERE id IN (:ids)")
  suspend fun deleteTracks(vararg ids: String)

  @Query("DELETE FROM lcArtists WHERE id IN (:ids)")
  suspend fun deleteArtists(vararg ids: String)

  @Query("DELETE FROM lcAlbums WHERE id IN (:ids)")
  suspend fun deleteAlbums(vararg ids: String)

  @Query("SELECT * from lcArtists ORDER BY addedAt DESC")
  suspend fun getArtists(): List<CollectionArtist>

  @Query("SELECT * from lcAlbums ORDER BY addedAt DESC")
  suspend fun getAlbums(): List<CollectionAlbum>

  @Query("SELECT * from lcTracks WHERE mainArtistId = :id")
  suspend fun getTracksByArtist(id: String): List<CollectionTrack>

  @Query("DELETE from lcTracks")
  suspend fun deleteTracks()

  @Query("DELETE from lcArtists")
  suspend fun deleteArtists()

  @Query("DELETE from lcAlbums")
  suspend fun deleteAlbums()

  @Query("DELETE from rootlist")
  suspend fun deleteRootList()

  @Query("DELETE from lcFilters")
  suspend fun deleteContentFilters()

  // Flows

  @Query("SELECT * from lcArtists WHERE id = :id")
  fun subscribeOnArtist(id: String): Flow<List<CollectionArtist>>

  @Query("SELECT * from lcAlbums WHERE id = :id")
  fun subscribeOnAlbum(id: String): Flow<List<CollectionAlbum>>

  @Query("SELECT * from lcTracks WHERE id = :id")
  fun subscribeOnTrack(id: String): Flow<List<CollectionTrack>>

  // Collection: Track

  @Query("SELECT * from lcTracks WHERE descriptors LIKE :tag ORDER BY addedAt DESC")
  suspend fun getTracksByTag(tag: String): List<CollectionTrack>

  @Query("SELECT * from lcTracks ORDER BY addedAt DESC")
  suspend fun getTracks(): List<CollectionTrack>

  @RawQuery
  suspend fun getTracksRaw(query: SupportSQLiteQuery): List<CollectionTrack>

  suspend fun getTracks(tag: String?, sortBy: TrackSorts, isAsc: Boolean): List<CollectionTrack> {
    val tagQuery = tag?.let { " WHERE descriptors LIKE ? " } ?: " "
    val sortOrder = if (when (sortBy) {
      TrackSorts.ByTime -> isAsc
      else -> !isAsc
    }) "ASC" else "DESC"

    val query = SimpleSQLiteQuery(
      "SELECT * from lcTracks${tagQuery}ORDER BY ${sortBy.order} $sortOrder",
      if (tag != null) arrayOf("%$tag%") else null
    )

    return getTracksRaw(query)
  }

  enum class TrackSorts (val order: String) {
    ByTime("addedAt"),
    ByName("name"),
    ByAlbum("albumName"),
    ByArtist("artistName")
  }
}