package bruhcollective.itaysonlab.jetispot.core.collection.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import bruhcollective.itaysonlab.jetispot.core.collection.db.model.LocalCollectionCategory
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtist
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtistMetadata
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionTrack
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem

@Database(
  entities = [
    LocalCollectionCategory::class,
    CollectionArtist::class,
    CollectionAlbum::class,
    CollectionTrack::class,
    CollectionArtistMetadata::class,
    CollectionRootlistItem::class
  ], version = 2, autoMigrations = [
    AutoMigration(from = 1, to = 2)
  ], exportSchema = true
)
abstract class LocalCollectionDatabase : RoomDatabase() {
  abstract fun dao(): LocalCollectionDao
}