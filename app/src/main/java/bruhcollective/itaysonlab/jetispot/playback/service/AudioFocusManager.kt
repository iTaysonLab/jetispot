package bruhcollective.itaysonlab.jetispot.playback.service

import android.content.Context
import android.media.AudioManager
import androidx.core.content.getSystemService
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import bruhcollective.itaysonlab.jetispot.core.SpPlayerManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioFocusManager @Inject constructor(
  @ApplicationContext private val appContext: Context,
  private val spPlayerManager: SpPlayerManager,
) : AudioManager.OnAudioFocusChangeListener {
  private val audioManager = appContext.getSystemService<AudioManager>()!!

  private val audioAttributes = AudioAttributesCompat.Builder().apply {
    setUsage(AudioAttributesCompat.USAGE_MEDIA)
    setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
  }.build()

  private val focusRequest = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN).apply {
    setAudioAttributes(audioAttributes)
    setOnAudioFocusChangeListener(this@AudioFocusManager)
  }.build()

  fun requestFocus() {
    AudioManagerCompat.requestAudioFocus(audioManager, focusRequest)
  }

  fun abandonFocus() {
    AudioManagerCompat.abandonAudioFocusRequest(audioManager, focusRequest)
  }

  override fun onAudioFocusChange(focusChange: Int) {
    when (focusChange) {
      AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
        spPlayerManager.playerNullable()?.pause()
      }
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
        spPlayerManager.playerNullable()?.volumeDown(24) // 64 total
      }
      AudioManager.AUDIOFOCUS_GAIN -> {
        spPlayerManager.playerNullable()?.play()
        spPlayerManager.playerNullable()?.volumeUp(24) // 64 total
      }
    }
  }
}