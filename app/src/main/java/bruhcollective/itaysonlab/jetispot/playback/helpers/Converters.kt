package bruhcollective.itaysonlab.jetispot.playback.helpers

import android.graphics.BitmapFactory
import androidx.media2.common.MediaMetadata
import xyz.gianlu.librespot.audio.MetadataWrapper
import xyz.gianlu.librespot.player.Player

fun MetadataWrapper.toMediaMetadata(player: Player) = MediaMetadata.Builder().apply {
  this.putText(MediaMetadata.METADATA_KEY_MEDIA_ID, id.toSpotifyUri())

  this.putText(MediaMetadata.METADATA_KEY_TITLE, name)
  this.putText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, name)

  this.putText(MediaMetadata.METADATA_KEY_ARTIST, artist)
  this.putText(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, artist)

  this.putText(MediaMetadata.METADATA_KEY_ALBUM, albumName)
  this.putLong(MediaMetadata.METADATA_KEY_DURATION, duration().toLong())

  this.putLong(MediaMetadata.METADATA_KEY_PLAYABLE, 1)

  val bmpRaw = player.currentCoverImage() ?: return@apply
  val bmp = BitmapFactory.decodeByteArray(bmpRaw, 0, bmpRaw.size)
  this.putBitmap(MediaMetadata.METADATA_KEY_ART, bmp)
  this.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bmp)
}.build()