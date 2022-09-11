package bruhcollective.itaysonlab.jetispot.playback.service

import android.annotation.SuppressLint
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.media3.common.*
import androidx.media3.common.text.CueGroup

@Suppress("DeprecatedCallableAddReplaceWith")
@SuppressLint("UnsafeOptInUsageError")
abstract class CleanedPlayer: BasePlayer() {
  // region We don't support MediaItem managing
  override fun setMediaItems(mediaItems: MutableList<MediaItem>, resetPosition: Boolean) {}
  override fun setMediaItems(mediaItems: MutableList<MediaItem>, startIndex: Int, startPositionMs: Long) {}
  override fun addMediaItems(index: Int, mediaItems: MutableList<MediaItem>) {}
  override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {}
  override fun removeMediaItems(fromIndex: Int, toIndex: Int) {}
  // endregion

  override fun getAvailableCommands() = Player.Commands.Builder().apply {
    addAll(Player.COMMAND_PREPARE)
    addAll(Player.COMMAND_PLAY_PAUSE)
    addAll(Player.COMMAND_SEEK_TO_PREVIOUS)
    addAll(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
    addAll(Player.COMMAND_SEEK_TO_NEXT)
    addAll(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)

    addAll(Player.COMMAND_SEEK_BACK)
    addAll(Player.COMMAND_SEEK_FORWARD)

    addAll(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)
  }.build()

  override fun getPlaybackSuppressionReason() = Player.PLAYBACK_SUPPRESSION_REASON_NONE
  override fun getPlayerError(): PlaybackException? = null

  override fun getSeekBackIncrement() = 0L
  override fun getSeekForwardIncrement() = 0L
  override fun getMaxSeekToPreviousPosition() = 5000L // TODO modify

  override fun setPlaybackParameters(playbackParameters: PlaybackParameters) {}
  override fun getPlaybackParameters() = PlaybackParameters.DEFAULT

  override fun getTrackSelectionParameters() = TrackSelectionParameters.DEFAULT_WITHOUT_CONTEXT
  override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) {}

  override fun getPlaylistMetadata() = MediaMetadata.EMPTY
  override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) {}

  override fun getCurrentTracks() = Tracks.EMPTY

  override fun getCurrentPeriodIndex() = currentMediaItemIndex

  @Deprecated("Deprecated in Java") override fun stop(reset: Boolean) {
    stop()
    if (reset) clearMediaItems()
  }

  // Not needed in Sp context

  override fun isPlayingAd() = false
  override fun getCurrentAdGroupIndex() = 0
  override fun getCurrentAdIndexInAdGroup() = 0

  override fun clearVideoSurface() {}
  override fun clearVideoSurface(surface: Surface?) {}
  override fun setVideoSurface(surface: Surface?) {}
  override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {}
  override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {}
  override fun setVideoSurfaceView(surfaceView: SurfaceView?) {}
  override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {}
  override fun setVideoTextureView(textureView: TextureView?) {}
  override fun clearVideoTextureView(textureView: TextureView?) {}
  override fun getVideoSize() = VideoSize.UNKNOWN
  override fun getCurrentCues() = CueGroup.EMPTY
}