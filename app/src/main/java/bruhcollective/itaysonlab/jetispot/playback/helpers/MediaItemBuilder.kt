package bruhcollective.itaysonlab.jetispot.playback.helpers

import android.graphics.Bitmap
import androidx.core.os.bundleOf
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata

fun MutableList<MediaItem>.addMediaItem (metadataBuilder: MediaMetadata.Builder.() -> Unit) = add(mediaItem(metadataBuilder = metadataBuilder))
fun mediaItem (startTime: Long = MediaItem.POSITION_UNKNOWN, endTime: Long = MediaItem.POSITION_UNKNOWN, metadataBuilder: MediaMetadata.Builder.() -> Unit) = MediaItem.Builder().setMetadata(MediaMetadata.Builder().also(metadataBuilder).build()).build()

fun MediaMetadata.Builder.extras (vararg extras: Pair<String, Any?>) = setExtras(bundleOf(*extras))
fun MediaMetadata.Builder.browsable (type: Long = MediaMetadata.BROWSABLE_TYPE_MIXED) = putLong(MediaMetadata.METADATA_KEY_BROWSABLE, type)
fun MediaMetadata.Builder.playable () = putLong(MediaMetadata.METADATA_KEY_PLAYABLE, 1)

private fun throwOnlyWrite(): Nothing = error("This property is designed only for writing")

var MediaMetadata.Builder.id: String?
    get() = throwOnlyWrite()
    set(value) { putText(MediaMetadata.METADATA_KEY_MEDIA_ID, value) }

var MediaMetadata.Builder.title: String?
    get() = throwOnlyWrite()
    set(value) { putText(MediaMetadata.METADATA_KEY_TITLE, value) }

var MediaMetadata.Builder.artist: String?
    get() = throwOnlyWrite()
    set(value) { putText(MediaMetadata.METADATA_KEY_ARTIST, value) }

var MediaMetadata.Builder.album: String?
    get() = throwOnlyWrite()
    set(value) { putText(MediaMetadata.METADATA_KEY_ALBUM, value) }

var MediaMetadata.Builder.duration: Long
    get() = throwOnlyWrite()
    set(value) { putLong(MediaMetadata.METADATA_KEY_DURATION, value) }

var MediaMetadata.Builder.subtitle: String?
    get() = throwOnlyWrite()
    set(value) { putText(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, value) }

var MediaMetadata.Builder.iconUri: String?
    get() = throwOnlyWrite()
    set(value) { putText(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, value) }

var MediaMetadata.Builder.iconBitmap: Bitmap?
    get() = throwOnlyWrite()
    set(value) { putBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON, value) }

var MediaMetadata.Builder.artBitmap: Bitmap?
    get() = throwOnlyWrite()
    set(value) { putBitmap(MediaMetadata.METADATA_KEY_ART, value) }

