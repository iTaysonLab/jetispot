package bruhcollective.itaysonlab.jetispot.playback.service

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.common.FlagSet
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import bruhcollective.itaysonlab.jetispot.playback.helpers.toMediaMetadata
import xyz.gianlu.librespot.audio.MetadataWrapper
import xyz.gianlu.librespot.metadata.PlayableId
import xyz.gianlu.librespot.player.Player

@SuppressLint("UnsafeOptInUsageError")
class SpServiceEventsListener(
  val player: SpPlayerWrapper
) : Player.EventsListener {
  override fun onContextChanged(p0: Player, p1: String) {

  }

  override fun onTrackChanged(p0: Player, p1: PlayableId, p2: MetadataWrapper?, userInitiated: Boolean) {
    player.state.trackPlaying = p1
    player.state.currentMediaItem = MediaItem.Builder().apply {
      setMediaId(player.state.trackPlaying?.toSpotifyUri() ?: "")
      setMediaMetadata(player.state.metadataWrapper?.toMediaMetadata() ?: MediaMetadata.EMPTY)
    }.build()
    player.runOnListeners {
      Log.d("SPM", "onTrackChanged $p1 / $p2 / $userInitiated")
      it.onPlaybackStateChanged(androidx.media3.common.Player.STATE_READY)
      it.onMediaItemTransition(player.state.currentMediaItem, androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO)
      it.onEvents(player, androidx.media3.common.Player.Events(
        FlagSet.Builder().addAll(
          androidx.media3.common.Player.EVENT_PLAYBACK_STATE_CHANGED,
          androidx.media3.common.Player.EVENT_MEDIA_ITEM_TRANSITION,
        ).build()
      ))
    }
  }

  override fun onPlaybackEnded(p0: Player) {
    player.state.playbackState = androidx.media3.common.Player.STATE_ENDED
    player.runOnListeners { it.onPlaybackStateChanged(player.state.playbackState) }
  }

  override fun onPlaybackPaused(p0: Player, trackTime: Long) = player.runOnListeners {
    it.onPlayWhenReadyChanged(false, androidx.media3.common.Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
    it.onIsPlayingChanged(false)
  }

  override fun onPlaybackResumed(p0: Player, trackTime: Long) = player.runOnListeners {
    it.onPlayWhenReadyChanged(true, androidx.media3.common.Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
    it.onIsPlayingChanged(true)
  }

  override fun onTrackSeeked(p0: Player, trackTime: Long) {

  }

  override fun onMetadataAvailable(p0: Player, p1: MetadataWrapper) {
    Log.d("SPM", "onMetadataAvailable $p1")
    player.state.metadataWrapper = p1
    player.state.currentMediaItem = MediaItem.Builder().apply {
      setMediaId(player.state.trackPlaying?.toSpotifyUri() ?: "")
      setMediaMetadata(player.state.metadataWrapper?.toMediaMetadata() ?: MediaMetadata.EMPTY)
    }.build()
    player.runOnListeners {
      it.onMediaMetadataChanged(p1.toMediaMetadata())
      it.onMediaItemTransition(player.state.currentMediaItem, androidx.media3.common.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO)
      it.onEvents(player, androidx.media3.common.Player.Events(
        FlagSet.Builder().addAll(
          androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED,
          androidx.media3.common.Player.EVENT_MEDIA_ITEM_TRANSITION,
        ).build()
      ))
    }
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
    player.state.isLoading = true
    player.runOnListeners { it.onIsLoadingChanged(true) }
  }

  override fun onFinishedLoading(p0: Player) {
    player.state.isLoading = false
    player.runOnListeners { it.onIsLoadingChanged(false) }
  }
}