package bruhcollective.itaysonlab.jetispot.playback.service

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import android.util.Log
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import bruhcollective.itaysonlab.jetispot.playback.helpers.toMediaMetadata
import com.spotify.context.ContextTrackOuterClass
import xyz.gianlu.librespot.audio.MetadataWrapper
import xyz.gianlu.librespot.metadata.PlayableId

@UnstableApi class SpPlayerWrapper(
  val service: SpPlaybackService
) : CleanedPlayer() {
  var state = State()

  private val playerNullable get() = service.spPlayerManager.playerNullable()
  private val player get() = service.spPlayerManager.player()

  private val reflect get() = service.spPlayerManager.reflect()
  private val playerAvailable get() = service.spPlayerManager.isPlayerAvailable() && player.isActive
  private val volumeManager = VolumeManager(service.applicationContext, object: VolumeManager.Listener {
    override fun onStreamTypeChanged(streamType: Int) {}

    override fun onStreamVolumeChanged(streamVolume: Int, streamMuted: Boolean) {
      runOnListeners { it.onDeviceVolumeChanged(streamVolume, streamMuted) }
    }
  })

  private val playbackThread =
    HandlerThread("SpPlayer:Playback", Process.THREAD_PRIORITY_AUDIO).also { it.start() }
  private val playbackHandler = Handler(playbackThread.looper)
  private val uiHandler = Handler(Looper.getMainLooper())

  private val listeners = mutableListOf<Player.Listener>()
  override fun getApplicationLooper(): Looper =
    Looper.getMainLooper() // allow calling from any thread

  override fun addListener(listener: Player.Listener) {
    listeners.add(listener)
  }

  override fun removeListener(listener: Player.Listener) {
    listeners.remove(listener)
  }

  fun runOnPlayback(block: () -> Unit) { playbackHandler.post(block) }
  fun runOnListeners(block: (Player.Listener) -> Unit) {
    uiHandler.post { listeners.forEach(block) }
  }

  fun getQueue() = mutableListOf<ContextTrackOuterClass.ContextTrack>().also {
    if (!playerAvailable) return@also
    val pt = player.tracks(true)
    it.addAll(pt.previous)
    it.add(pt.current)
    it.addAll(pt.next)
  }

  override fun stop() = runOnPlayback {
    player.pause()
  }

  override fun prepare() {
    // it should be prepared before this point
  }

  override fun getPlaybackState() = state.playbackState

  override fun getMediaMetadata(): MediaMetadata {
    Log.d("SCM", "getMediaMetadata ${state.metadataWrapper}")
    return state.metadataWrapper?.toMediaMetadata() ?: MediaMetadata.EMPTY
  }

  override fun getCurrentTimeline() = spTimeline

  override fun getCurrentMediaItemIndex(): Int {
    if (!playerAvailable) return 0
    return player.tracks(true).previous.size
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

  override fun seekTo(mediaItemIndex: Int, positionMs: Long) = runOnPlayback {
    player.seek(positionMs.toInt())
  }

  override fun release() {
    volumeManager.release()
    runOnPlayback {
      playbackThread.quit()
      // service.spPlayerManager.release()
    }
  }

  override fun getDuration() = playerNullable?.currentMetadata()?.duration()?.toLong() ?: C.TIME_UNSET
  override fun getCurrentPosition() = playerNullable?.time()?.toLong() ?: 0L // TODO * 1000?

  override fun getBufferedPosition() = 0L
  override fun getTotalBufferedDuration() = 0L
  override fun getContentPosition() = currentPosition
  override fun getContentBufferedPosition() = bufferedPosition

  override fun getAudioAttributes() = AudioAttributes.Builder().apply {
    setContentType(C.CONTENT_TYPE_MUSIC)
    setUsage(C.USAGE_MEDIA)
  }.build()

  override fun setVolume(volume: Float) {
    // 0.01 -> 1%, 0.5 - 50%
    //player.setVolume((volume * 100).toInt())
  }

  override fun getVolume() = 1f

  override fun getDeviceInfo() = DeviceInfo(DeviceInfo.PLAYBACK_TYPE_REMOTE, volumeManager.getMinVolume(), volumeManager.getMaxVolume())
  override fun getDeviceVolume() = volumeManager.volume
  override fun isDeviceMuted() = volumeManager.muted
  override fun setDeviceVolume(volume: Int) = volumeManager.setVolume(volume)
  override fun increaseDeviceVolume() = volumeManager.increase()
  override fun decreaseDeviceVolume() = volumeManager.decrease()
  override fun setDeviceMuted(muted: Boolean) = volumeManager.setMuted(muted)

  //

  val spTimeline = @SuppressLint("UnsafeOptInUsageError") object: Timeline() {
    override fun getWindowCount() = getQueue().size
    override fun getPeriodCount() = getQueue().size

    override fun getWindow(
      windowIndex: Int,
      window: Window,
      defaultPositionProjectionUs: Long
    ) = window.apply {
      val track = getQueue()[windowIndex]
      Log.d("SCM", "track2 ${track.metadataMap}")
      set(
        track.uid,
        state.currentMediaItem,
        null,
        0,
        0,
        0,
        true,
        false,
        null,
        0,
        track.metadataMap.getOrElse("duration") { "0" }.toLong(),
        windowIndex,
        windowIndex,
        0
      )
    }

    override fun getPeriod(periodIndex: Int, period: Period, setIds: Boolean) = period.apply {
      val track = getQueue()[periodIndex]
      Log.d("SCM", "track ${track.metadataMap}")
      set(
        track.uid,
        track.uid,
        periodIndex,
        track.metadataMap.getOrElse("duration") { "0" }.toLong(),
        0
      )
    }

    override fun getIndexOfPeriod(uid: Any): Int {
      TODO("Not yet implemented")
    }

    override fun getUidOfPeriod(periodIndex: Int): Any {
      TODO("Not yet implemented")
    }
  }

  data class State(
    var playbackState: Int = STATE_IDLE,
    var isLoading: Boolean = false,
    var isPlaying: Boolean = false,
    var metadataWrapper: MetadataWrapper? = null,
    var trackPlaying: PlayableId? = null,
    var currentMediaItem: MediaItem? = null
  )
}