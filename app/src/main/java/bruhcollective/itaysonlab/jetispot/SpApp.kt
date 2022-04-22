package bruhcollective.itaysonlab.jetispot

import android.app.Application
import bruhcollective.itaysonlab.jetispot.playback.sp.AndroidNativeDecoder
import dagger.hilt.android.HiltAndroidApp
import xyz.gianlu.librespot.audio.decoders.Decoders
import xyz.gianlu.librespot.audio.format.SuperAudioFormat

@HiltAndroidApp
class SpApp: Application() {
  init {
    Decoders.registerDecoder(SuperAudioFormat.VORBIS, AndroidNativeDecoder::class.java)
    Decoders.registerDecoder(SuperAudioFormat.MP3, AndroidNativeDecoder::class.java)
  }
}