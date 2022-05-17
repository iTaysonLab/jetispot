package bruhcollective.itaysonlab.jetispot.core.collection.db.model2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lcFilters")
data class CollectionContentFilter(
  @PrimaryKey val name: String,
  val query: String
)
