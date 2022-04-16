package bruhcollective.itaysonlab.jetispot.playback.helpers

import androidx.media3.common.MediaMetadata
import com.spotify.context.ContextTrackOuterClass
import xyz.gianlu.librespot.audio.MetadataWrapper

fun MetadataWrapper.toMediaMetadata() = MediaMetadata.Builder().apply {
  setTitle(this@toMediaMetadata.name)
  setDisplayTitle(this@toMediaMetadata.name)

  setArtist(this@toMediaMetadata.artist)
  setAlbumArtist(this@toMediaMetadata.artist)
  setAlbumTitle(this@toMediaMetadata.albumName)

  setIsPlayable(true)
}.build()

fun ContextTrackOuterClass.ContextTrack.toMediaMetadata() = MediaMetadata.Builder().apply {

}.build()