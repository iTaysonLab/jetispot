package bruhcollective.itaysonlab.jetispot.playback.service

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.media.AudioAttributesCompat
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import com.spotify.context.ContextTrackOuterClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.guava.future
import xyz.gianlu.librespot.audio.MetadataWrapper
import java.util.concurrent.Executors

class SpPlayerWrapper(
  private val service: SpPlaybackService
) : SessionPlayer() {
  val playbackExecutor = Executors.newSingleThreadExecutor()
  val state = State()

  private val playbackScope = CoroutineScope(playbackExecutor.asCoroutineDispatcher() + SupervisorJob())

  private val playerNullable get() = service.spPlayerManager.playerNullable()
  private val player get() = service.spPlayerManager.player()

  private val reflect get() = service.spPlayerManager.reflect()
  private val playerAvailable get() = service.spPlayerManager.isPlayerAvailable() && player.isActive

  private val uiHandler = Handler(Looper.getMainLooper())

  fun runOnPlayback(func: () -> Unit) = playbackExecutor.submit(func)
  fun runOnListeners(func: (PlayerCallback) -> Unit) {
    callbacks.forEach { it.second.execute { func(it.first) } }
  }

  private fun getCurrentTrack(): ContextTrackOuterClass.ContextTrack = player.tracks(false).current
  private fun getQueue(): List<ContextTrackOuterClass.ContextTrack> = mutableListOf<ContextTrackOuterClass.ContextTrack>().also {
    if (!playerAvailable) return@also
    val pt = player.tracks(true)
    it.addAll(pt.previous)
    it.add(pt.current)
    it.addAll(pt.next)
  }

  override fun play() = createListenableFuture { player.play() }
  override fun pause() = createListenableFuture { player.pause() }

  override fun setPlaybackSpeed(playbackSpeed: Float) = emptyFuture()
  override fun setAudioAttributes(attributes: AudioAttributesCompat) = emptyFuture()
  override fun prepare() = emptyFuture() // no need

  override fun seekTo(position: Long) = createListenableFuture { player.seek(position.toInt()) }

  override fun getPlayerState() = state.playbackState
  override fun getCurrentPosition() = player.time().toLong()
  override fun getDuration() = state.currentTrack?.duration()?.toLong() ?: UNKNOWN_TIME

  override fun getBufferedPosition() = UNKNOWN_TIME
  override fun getBufferingState() = BUFFERING_STATE_UNKNOWN
  override fun getPlaybackSpeed() = 1f

  override fun setPlaylist(
    list: MutableList<MediaItem>,
    metadata: MediaMetadata?
  ) = unsupportedFuture()

  override fun getAudioAttributes(): AudioAttributesCompat = AudioAttributesCompat.Builder()
    .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
    .setUsage(AudioAttributesCompat.USAGE_MEDIA)
    .build()

  override fun setMediaItem(item: MediaItem) = unsupportedFuture()
  override fun addPlaylistItem(index: Int, item: MediaItem) = unsupportedFuture()
  override fun removePlaylistItem(index: Int) = unsupportedFuture()
  override fun replacePlaylistItem(index: Int, item: MediaItem) = unsupportedFuture()

  override fun skipToPreviousPlaylistItem() = createListenableFuture {
    player.previous()
  }

  override fun skipToNextPlaylistItem() = createListenableFuture {
    player.next()
  }

  override fun updatePlaylistMetadata(metadata: MediaMetadata?) = createListenableFuture {  }

  override fun setRepeatMode(repeatMode: Int) = createListenableFuture {
    player.setRepeat(repeatMode == REPEAT_MODE_ONE, repeatMode == REPEAT_MODE_ALL)
  }

  override fun setShuffleMode(shuffleMode: Int) = createListenableFuture {
    player.setShuffle(shuffleMode == SHUFFLE_MODE_ALL)
  }

  override fun getPlaylist(): MutableList<MediaItem>? = null // TODO
  override fun getPlaylistMetadata() = MediaMetadata.Builder().build() // TODO
  override fun skipToPlaylistItem(index: Int) = unsupportedFuture() // TODO

  override fun getRepeatMode() = reflect.getRepeatMode()
  override fun getShuffleMode() = reflect.getShuffleMode()
  override fun getCurrentMediaItem() = state.currentMediaItem
  override fun getCurrentMediaItemIndex() = if (playerAvailable) getQueue().indexOf(getCurrentTrack()) else 0
  override fun getPreviousMediaItemIndex() = if (playerAvailable) currentMediaItemIndex - 1 else 0
  override fun getNextMediaItemIndex() = if (playerAvailable) currentMediaItemIndex + 1 else 0

  @SuppressLint("RestrictedApi")
  private fun createListenableFuture(action: suspend () -> Unit) = playbackScope.future {
    return@future try {
      action()
      PlayerResult(PlayerResult.RESULT_SUCCESS, currentMediaItem)
    } catch (e: Exception) {
      e.printStackTrace()
      PlayerResult(PlayerResult.RESULT_ERROR_IO, null)
    }
  }

  @SuppressLint("RestrictedApi") private fun unsupportedFuture() = playbackScope.future { PlayerResult(PlayerResult.RESULT_ERROR_NOT_SUPPORTED, null) }
  @SuppressLint("RestrictedApi") private fun emptyFuture() = playbackScope.future { PlayerResult(PlayerResult.RESULT_SUCCESS, null) }

  data class State(
    var playbackState: Int = PLAYER_STATE_IDLE,
    var currentTrack: MetadataWrapper? = null,
    var currentMediaItem: MediaItem? = null
  )
}