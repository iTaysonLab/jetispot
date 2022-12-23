package bruhcollective.itaysonlab.jetispot

import android.app.Application
import android.os.Build
import bruhcollective.itaysonlab.jetispot.playback.sp.AndroidNativeDecoder
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import org.slf4j.impl.HandroidLoggerAdapter
import xyz.gianlu.librespot.audio.decoders.Decoders
import xyz.gianlu.librespot.audio.format.SuperAudioFormat

@HiltAndroidApp
class SpApp: Application() {
  init {
    Decoders.registerDecoder(SuperAudioFormat.VORBIS, AndroidNativeDecoder::class.java)
    Decoders.registerDecoder(SuperAudioFormat.MP3, AndroidNativeDecoder::class.java)

    HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG
    HandroidLoggerAdapter.ANDROID_API_LEVEL = Build.VERSION.SDK_INT
    HandroidLoggerAdapter.APP_NAME = "SpApp"
  }

  override fun onCreate() {
    super.onCreate()
    MMKV.initialize(this, "${filesDir.absolutePath}/spa_meta")
  }
}