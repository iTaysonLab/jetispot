package bruhcollective.itaysonlab.jetispot.core.collection.db.model2

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import xyz.gianlu.librespot.metadata.SpotifyId

@Entity(tableName = "lcArtists")
data class CollectionArtist(
  @PrimaryKey val id: String,
  val uri: String,
  val name: String,
  val picture: String,
  val addedAt: Int
): CollectionModel