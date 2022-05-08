package bruhcollective.itaysonlab.jetispot.core.collection.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lcTypes")
data class LocalCollectionCategory(
  @PrimaryKey val type: String,
  val syncToken: String
)