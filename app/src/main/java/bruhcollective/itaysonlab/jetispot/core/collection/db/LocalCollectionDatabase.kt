package bruhcollective.itaysonlab.jetispot.core.collection.db

import androidx.room.Database
import androidx.room.RoomDatabase
import bruhcollective.itaysonlab.jetispot.core.collection.db.model.LocalCollectionCategory
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtist
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtistMetadata
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionTrack

@Database(
  entities = [
    LocalCollectionCategory::class,
    CollectionArtist::class,
    CollectionAlbum::class,
    CollectionTrack::class,
    CollectionArtistMetadata::class,
  ], version = 1
)
abstract class LocalCollectionDatabase : RoomDatabase() {
  abstract fun dao(): LocalCollectionDao
}