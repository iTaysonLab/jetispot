package bruhcollective.itaysonlab.jetispot.core.collection.db.model2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lcAlbums")
data class CollectionAlbum(
  @PrimaryKey val id: String,
  val uri: String,
  val name: String,
  val rawArtistsData: String,
  val picture: String,
  val addedAt: Int
): CollectionEntry {
  override fun ceId() = id
  override fun ceUri() = uri
  override fun ceTimestamp() = addedAt.toLong()
}