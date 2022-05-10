package bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rootlist")
data class CollectionRootlistItem(
  @PrimaryKey val uri: String,
  val timestamp: Long,
  val name: String,
  val ownerUsername: String,
  val picture: String
)