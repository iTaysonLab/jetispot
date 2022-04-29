package bruhcollective.itaysonlab.jetispot.playback.helpers

import androidx.compose.ui.graphics.asImageBitmap
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata

class MediaItemWrapper(
  private val item: MediaItem? = null
) {
  val hasMetadata get() = item != null

  val title get() = item?.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Title"
  val artist get() = item?.metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
  val album get() = item?.metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: "Unknown Album"
  val duration get() = item?.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L

  val artwork get() = item?.metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
  val artworkCompose by lazy { artwork?.asImageBitmap() }
}