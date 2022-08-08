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

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addPins(vararg items: CollectionPinnedItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addShows(vararg items: CollectionShow)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addEpisodes(vararg items: CollectionEpisode)

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

  @Query("SELECT * from lcShows ORDER BY addedAt DESC")
  suspend fun getShows(): List<CollectionShow>

  @Query("SELECT * from lcEpisodes ORDER BY addedAt DESC")
  suspend fun getEpisodes(): List<CollectionEpisode>

  @Query("SELECT * from lcTracks WHERE mainArtistId = :id")
  suspend fun getTracksByArtist(id: String): List<CollectionTrack>

  @Query("SELECT * from lcPins ORDER BY addedAt ASC")
  suspend fun getPins(): List<CollectionPinnedItem>

  @Query("SELECT * from rootlist ORDER BY timestamp DESC")
  suspend fun getRootlist(): List<CollectionRootlistItem>

  @Query("DELETE from lcTracks")
  suspend fun deleteTracks()

  @Query("DELETE from lcArtists")
  suspend fun deleteArtists()

  @Query("DELETE from lcAlbums")
  suspend fun deleteAlbums()

  @Query("DELETE from rootlist")
  suspend fun deleteRootList()

  @Query("DELETE FROM rootlist WHERE uri IN (:uris)")
  suspend fun deleteRootList(vararg uris: String)

  @Query("DELETE from lcFilters")
  suspend fun deleteContentFilters()

  @Query("DELETE FROM lcPins WHERE uri IN (:uris)")
  suspend fun deletePins(vararg uris: String)

  @Query("DELETE FROM lcShows WHERE uri IN (:uris)")
  suspend fun deleteShows(vararg uris: String)

  @Query("DELETE FROM lcEpisodes WHERE uri IN (:uris)")
  suspend fun deleteEpisodes(vararg uris: String)

  //

  @Query("SELECT * from lcAlbums WHERE uri = :uri")
  suspend fun getAlbum(uri: String): List<CollectionAlbum>

  @Query("SELECT * from lcTracks WHERE uri = :uri")
  suspend fun getTrack(uri: String): List<CollectionTrack>

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

  @Query("SELECT COUNT(id) FROM lcTracks")
  suspend fun trackCount(): Int

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
    ByArtist("mainArtistName")
  }
}