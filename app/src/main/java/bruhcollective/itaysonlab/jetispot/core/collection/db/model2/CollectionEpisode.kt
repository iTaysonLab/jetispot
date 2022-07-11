package bruhcollective.itaysonlab.jetispot.core.collection.db.model2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lcEpisodes")
data class CollectionEpisode(
  @PrimaryKey val uri: String,
  val name: String,
  val description: String,
  val showName: String,
  val showUri: String,
  val picture: String,
  val addedAt: Int
): CollectionEntry {
  override fun ceId() = uri
  override fun ceUri() = uri
  override fun ceTimestamp() = addedAt.toLong()
}