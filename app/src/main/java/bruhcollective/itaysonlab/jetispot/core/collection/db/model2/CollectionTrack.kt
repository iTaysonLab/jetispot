package bruhcollective.itaysonlab.jetispot.core.collection.db.model2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lcTracks")
data class CollectionTrack(
  @PrimaryKey val id: String,
  val uri: String,
  val name: String,
  val albumId: String,
  val albumName: String,
  @ColumnInfo(defaultValue = "") val mainArtistName: String,
  val mainArtistId: String, // for metadata&joins
  val rawArtistsData: String, // for UI, format: ID=Name (example: 1=Artist|2=Artist2)
  val hasLyrics: Boolean,
  val isExplicit: Boolean,
  val duration: Int,
  val picture: String,
  @ColumnInfo(defaultValue = "") val descriptors: String, // indie|modern rock, for example
  val addedAt: Int
)