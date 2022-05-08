package bruhcollective.itaysonlab.jetispot.core.collection.db.model2

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import xyz.gianlu.librespot.metadata.SpotifyId

@Entity(tableName = "lcMetaArtists")
data class CollectionArtistMetadata(
  @PrimaryKey val id: String,
  val genres: String, // genre 1|genre 2
)