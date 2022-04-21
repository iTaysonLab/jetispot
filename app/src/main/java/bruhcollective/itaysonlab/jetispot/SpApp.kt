package bruhcollective.itaysonlab.jetispot

import android.app.Application
import android.os.Build
import bruhcollective.itaysonlab.jetispot.core.SpConfigurationManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.playback.sp.AndroidNativeDecoder
import bruhcollective.itaysonlab.jetispot.playback.sp.TremoloVorbisDecoder
import dagger.hilt.android.HiltAndroidApp
import xyz.gianlu.librespot.audio.decoders.Decoders
import xyz.gianlu.librespot.audio.format.SuperAudioFormat
import javax.inject.Inject

@HiltAndroidApp
class SpApp: Application() {
  @Inject lateinit var spConfigurationManager: SpConfigurationManager

  init {
    Decoders.registerDecoder(SuperAudioFormat.VORBIS, AndroidNativeDecoder::class.java)
    Decoders.registerDecoder(SuperAudioFormat.MP3, AndroidNativeDecoder::class.java)
    if (isArm()) Decoders.registerDecoder(SuperAudioFormat.VORBIS, 0, TremoloVorbisDecoder::class.java)
  }

  private fun isArm() = Build.SUPPORTED_ABIS.firstOrNull { it.contains("arm") } != null
}