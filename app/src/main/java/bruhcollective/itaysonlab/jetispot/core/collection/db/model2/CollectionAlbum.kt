package bruhcollective.itaysonlab.jetispot.core.collection.db.model2

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import xyz.gianlu.librespot.metadata.SpotifyId

@Entity(tableName = "lcAlbums")
data class CollectionAlbum(
  @PrimaryKey val id: String,
  val uri: String,
  val name: String,
  val rawArtistsData: String,
  val picture: String,
  val addedAt: Int
): CollectionModel