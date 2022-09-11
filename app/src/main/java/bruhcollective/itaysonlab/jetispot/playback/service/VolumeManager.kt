package bruhcollective.itaysonlab.jetispot.playback.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import androidx.media3.common.C

@SuppressLint("UnsafeOptInUsageError")
class VolumeManager (
  private val context: Context,
  private val listener: Listener
) {
  private val TAG = "VolumeManager"
  private val VOLUME_FLAGS = AudioManager.FLAG_SHOW_UI
  
  private val audioManager = context.getSystemService<AudioManager>()!!
  private val streamType = C.STREAM_TYPE_DEFAULT
  private val receiver = VolumeChangeReceiver()
  
  var volume: Int
    private set

  var muted: Boolean
    private set

  init {
    volume = getVolumeFromManager()
    muted = getMutedFromManager()
    context.registerReceiver(receiver, IntentFilter("android.media.VOLUME_CHANGED_ACTION"))
  }

  fun increase() {
    if (volume >= getMaxVolume()) {
      return
    }
    audioManager.adjustStreamVolume(streamType, AudioManager.ADJUST_RAISE, VOLUME_FLAGS)
    updateVolumeAndNotifyIfChanged()
  }

  fun decrease() {
    if (volume <= getMinVolume()) {
      return
    }
    audioManager.adjustStreamVolume(streamType, AudioManager.ADJUST_LOWER, VOLUME_FLAGS)
    updateVolumeAndNotifyIfChanged()
  }

  /**
   * Gets the minimum volume for the current audio stream. It can be changed if {@link
   * #setStreamType(int)} is called.
   */
  fun getMinVolume() = if (Build.VERSION.SDK_INT >= 28) audioManager.getStreamMinVolume(streamType) else 0

  /**
   * Gets the maximum volume for the current audio stream. It can be changed if {@link
   * #setStreamType(int)} is called.
   */
  fun getMaxVolume() = audioManager.getStreamMaxVolume(streamType)

  /** Sets the mute state of the current audio stream. */
  fun setMuted(muted: Boolean) {
    if (Build.VERSION.SDK_INT >= 23) {
      audioManager.adjustStreamVolume(streamType, if (muted) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE, VOLUME_FLAGS)
    } else {
      audioManager.setStreamMute(streamType, muted)
    }
    updateVolumeAndNotifyIfChanged()
  }

  /**
   * Sets the volume with the given value for the current audio stream. The value should be between
   * {@link #getMinVolume()} and {@link #getMaxVolume()}, otherwise it will be ignored.
   */
  fun setVolume(volume: Int) {
    if (volume < getMinVolume() || volume > getMaxVolume()) return
    audioManager.setStreamVolume(streamType, volume, VOLUME_FLAGS)
    updateVolumeAndNotifyIfChanged()
  }

  private fun getVolumeFromManager(): Int {
    // AudioManager#getStreamVolume(int) throws an exception on some devices. See
    // https://github.com/google/ExoPlayer/issues/8191.
    return try {
      audioManager.getStreamVolume(streamType)
    } catch (e: RuntimeException) {
      Log.w(TAG, "Could not retrieve stream volume for stream type $streamType", e)
      audioManager.getStreamMaxVolume(streamType)
    }
  }

  private fun getMutedFromManager(): Boolean {
    return if (Build.VERSION.SDK_INT >= 23) {
      audioManager.isStreamMute(streamType)
    } else {
      getVolumeFromManager() == 0
    }
  }

  private fun updateVolumeAndNotifyIfChanged() {
    val newVolume = getVolumeFromManager()
    val newMuted = getMutedFromManager()
    if (volume != newVolume || muted != newMuted) {
      volume = newVolume
      muted = newMuted
      listener.onStreamVolumeChanged(newVolume, newMuted)
    }
  }

  fun release () {
    context.unregisterReceiver(receiver)
  }

  interface Listener {
    fun onStreamTypeChanged(@C.StreamType streamType: Int)
    fun onStreamVolumeChanged(streamVolume: Int, streamMuted: Boolean)
  }

  inner class VolumeChangeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      updateVolumeAndNotifyIfChanged()
    }
  }
}