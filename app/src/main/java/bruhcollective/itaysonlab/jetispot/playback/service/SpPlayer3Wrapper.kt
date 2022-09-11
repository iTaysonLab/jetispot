package bruhcollective.itaysonlab.jetispot.playback.service

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import com.spotify.context.ContextTrackOuterClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import xyz.gianlu.librespot.audio.MetadataWrapper
import java.util.concurrent.Executors

@UnstableApi class SpPlayer3Wrapper(
  private val service: SpPlaybackService
) : CleanedPlayer() {
  private val volumeManager = VolumeManager(service.applicationContext, object: VolumeManager.Listener {
    override fun onStreamTypeChanged(streamType: Int) {}
    override fun onStreamVolumeChanged(streamVolume: Int, streamMuted: Boolean) {
      runOnListeners { it.onDeviceVolumeChanged(streamVolume, streamMuted) }
    }
  })

  val playbackExecutor = Executors.newSingleThreadExecutor()
  val state = State()
  val audioFocus get() = service.audioFocusManager

  private val playbackScope = CoroutineScope(playbackExecutor.asCoroutineDispatcher() + SupervisorJob())

  private val playerNullable get() = service.spPlayerManager.playerNullable()
  private val player get() = service.spPlayerManager.player()

  private val reflect get() = service.spPlayerManager.reflect()
  private val playerAvailable get() = service.spPlayerManager.isPlayerAvailable() && player.isActive

  private val playbackThread = HandlerThread("SpPlayer:Playback", android.os.Process.THREAD_PRIORITY_AUDIO).also { it.start() }
  private val playbackHandler = Handler(playbackThread.looper)

  private val uiHandler = Handler(Looper.getMainLooper())
  private var listeners = mutableListOf<Player.Listener>()

  fun runOnPlayback(func: () -> Unit) { playbackHandler.post(func) }
  fun runOnListeners(func: (Player.Listener) -> Unit) { listeners.forEach(func) }

  private fun getCurrentTrack(): ContextTrackOuterClass.ContextTrack = player.tracks(false).current
  private fun getQueue(): List<ContextTrackOuterClass.ContextTrack> = mutableListOf<ContextTrackOuterClass.ContextTrack>().also {
    if (!playerAvailable) return@also
    val pt = player.tracks(true)
    it.addAll(pt.previous)
    it.add(pt.current)
    it.addAll(pt.next)
  }

  data class State(
    var playbackState: Int = STATE_IDLE,
    var currentTrack: MetadataWrapper? = null,
    var currentMediaItem: MediaItem? = null,
    var currentContextMetadata: MediaMetadata? = null
  ) {
    val isPlaying get() = playbackState == STATE_READY
  }

  override fun stop() {
    runOnPlayback { player.pause() }
  }

  override fun getApplicationLooper(): Looper = Looper.getMainLooper()

  override fun addListener(listener: Player.Listener) {
    listeners.add(listener)
  }

  override fun removeListener(listener: Player.Listener) {
    listeners.remove(listener)
  }

  override fun prepare() {
    // it should be prepared before this point
  }

  override fun getPlaybackState(): Int {
    TODO("Not yet implemented")
  }

  override fun seekTo(mediaItemIndex: Int, positionMs: Long) {
    player.seek(positionMs.toInt())
  }

  override fun release() {
    volumeManager.release()
    runOnPlayback {
      playbackThread.quit()
      // service.spPlayerManager.release()
    }
  }

  override fun setPlayWhenReady(playWhenReady: Boolean) = runOnPlayback {
    if (playWhenReady) {
      player.play()
    } else {
      player.pause()
    }
  }

  override fun getPlayWhenReady() = state.isPlaying

  override fun setRepeatMode(repeatMode: Int) = runOnPlayback {
    player.setRepeat(
      repeatMode == REPEAT_MODE_ONE,
      repeatMode == REPEAT_MODE_ALL
    )
  }

  override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) = runOnPlayback {
    player.setShuffle(shuffleModeEnabled)
  }

  override fun getShuffleModeEnabled() = if (playerAvailable) reflect.isShuffleModeEnabled() else false
  override fun getRepeatMode() = if (playerAvailable) reflect.getRepeatMode() else REPEAT_MODE_OFF
  override fun isLoading() = state.isLoading

  override fun getMediaMetadata(): MediaMetadata {
    TODO("Not yet implemented")
  }

  override fun getCurrentTimeline(): Timeline {
    TODO("Not yet implemented")
  }

  override fun getCurrentMediaItemIndex(): Int {
    TODO("Not yet implemented")
  }

  override fun getAudioAttributes(): AudioAttributes {
    TODO("Not yet implemented")
  }

  override fun getDuration() = playerNullable?.currentMetadata()?.duration()?.toLong() ?: C.TIME_UNSET
  override fun getCurrentPosition() = playerNullable?.time()?.toLong() ?: 0L // TODO * 1000?

  override fun getBufferedPosition() = 0L
  override fun getTotalBufferedDuration() = 0L
  override fun getContentPosition() = currentPosition
  override fun getContentBufferedPosition() = bufferedPosition

  override fun setVolume(volume: Float) {
    // TODO
  }

  override fun getVolume() = 1f
  override fun getDeviceInfo() = DeviceInfo(DeviceInfo.PLAYBACK_TYPE_REMOTE, volumeManager.getMinVolume(), volumeManager.getMaxVolume())
  override fun getDeviceVolume() = volumeManager.volume
  override fun isDeviceMuted() = volumeManager.muted
  override fun setDeviceVolume(volume: Int) = volumeManager.setVolume(volume)
  override fun increaseDeviceVolume() = volumeManager.increase()
  override fun decreaseDeviceVolume() = volumeManager.decrease()
  override fun setDeviceMuted(muted: Boolean) = volumeManager.setMuted(muted)
}