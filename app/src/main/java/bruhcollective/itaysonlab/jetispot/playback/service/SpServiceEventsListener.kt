package bruhcollective.itaysonlab.jetispot.playback.service

import android.graphics.BitmapFactory
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import bruhcollective.itaysonlab.jetispot.core.util.Log
import bruhcollective.itaysonlab.jetispot.playback.helpers.*
import com.spotify.context.ContextTrackOuterClass
import xyz.gianlu.librespot.audio.MetadataWrapper
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.PlayableId
import xyz.gianlu.librespot.player.Player

class SpServiceEventsListener(
  val player: SpPlayerWrapper
) : Player.EventsListener {
  override fun onContextChanged(p0: Player, p1: String) {
    Log.e("SpService", "onContextChanged => $p1")
    /* player.state.currentContextMetadata = MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_MEDIA_URI, p1).build()
    player.runOnListeners {
      it.onPlaylistMetadataChanged(player, player.state.currentContextMetadata)
    }*/
  }

  override fun onContextDescriptionChanged(p0: Player, description: String?) {
    Log.e("SpService", "onContextDescriptionChanged => $description")
    player.state.currentContextMetadata = MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_MEDIA_URI, p0.stateWrapper.contextUri).putString(MediaMetadata.METADATA_KEY_TITLE, description).build()
    player.runOnListeners {
      it.onPlaylistMetadataChanged(player, player.state.currentContextMetadata)
    }
  }

  override fun onTrackChanged(p0: Player, p1: PlayableId, p2: MetadataWrapper?, userInitiated: Boolean) {
    // no ID yet
  }

  override fun onPlaybackEnded(p0: Player) {
    player.state.playbackState = SessionPlayer.PLAYER_STATE_IDLE
    player.runOnListeners { it.onPlaybackCompleted(player) }
  }

  override fun onPlaybackPaused(p0: Player, trackTime: Long) {
    player.audioFocus.abandonFocus()
    player.state.playbackState = SessionPlayer.PLAYER_STATE_PAUSED
    player.runOnListeners { it.onPlayerStateChanged(player, player.state.playbackState) }
  }

  override fun onPlaybackResumed(p0: Player, trackTime: Long) {
    player.audioFocus.requestFocus()
    player.state.playbackState = SessionPlayer.PLAYER_STATE_PLAYING
    player.runOnListeners { it.onPlayerStateChanged(player, player.state.playbackState) }
  }

  override fun onPlaybackFailed(p0: Player, p1: Exception) {
    Log.e("SpService", "onPlaybackFailed => ${p1.message}")
  }

  override fun onTrackSeeked(p0: Player, trackTime: Long) {
    // player.runOnListeners { it.onSeekCompleted(player, trackTime) }
  }

  override fun onMetadataAvailable(p0: Player, p1: MetadataWrapper) {
    Log.d("SpService:OMA", p0.stateWrapper.state.toString())

    player.state.currentTrack = p1

    player.state.currentMediaItem = mediaItem(startTime = 0L, endTime = p1.duration().toLong()) {
      id = p1.id.toSpotifyUri()
      title = p1.name
      artist = p1.artist
      album = p1.albumName
      duration = p1.duration().toLong()

      artBitmap = try {
        p0.currentCoverImage()?.let { bmpRaw -> BitmapFactory.decodeByteArray(bmpRaw, 0, bmpRaw.size) }
      } catch (e: Exception) {
        null
      }

      playable()
    }

    player.runOnListeners { it.onCurrentMediaItemChanged(player, player.state.currentMediaItem) }
  }

  override fun onPlaybackHaltStateChanged(p0: Player, p1: Boolean, p2: Long) {

  }

  override fun onInactiveSession(p0: Player, p1: Boolean) {

  }

  override fun onVolumeChanged(p0: Player, p1: Float) {

  }

  override fun onPanicState(p0: Player) {

  }

  override fun onStartedLoading(p0: Player) {

  }

  override fun onQueueChanged(
    p0: Player,
    queue: MutableList<ContextTrackOuterClass.ContextTrack>
  ) {
    Log.d("SpService", "onQueueChanged")

    val queueX = queue.map { MediaItem.Builder().apply {
      setMetadata(MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_MEDIA_ID, Utils.bytesToHex(it.gid)).putString(MediaMetadata.METADATA_KEY_MEDIA_URI, it.uri).build())
    }.build() }

    player.runOnListeners {
      it.onPlaylistChanged(player, queueX, player.state.currentContextMetadata)
    }
  }

  override fun onFinishedLoading(p0: Player) {
    player.state.playbackState = SessionPlayer.PLAYER_STATE_PLAYING
  }
}