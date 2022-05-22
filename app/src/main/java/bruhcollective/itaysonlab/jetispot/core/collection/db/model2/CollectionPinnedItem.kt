package bruhcollective.itaysonlab.jetispot.core.collection.db.model2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lcPins")
data class CollectionPinnedItem(
  @PrimaryKey val uri: String,
  val name: String,
  val subtitle: String,
  val picture: String,
  val addedAt: Int
): CollectionEntry {
  override fun ceId() = uri
  override fun ceTimestamp() = addedAt.toLong()
}