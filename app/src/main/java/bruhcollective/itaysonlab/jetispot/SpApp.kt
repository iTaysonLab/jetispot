package bruhcollective.itaysonlab.jetispot

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import bruhcollective.itaysonlab.jetispot.playback.sp.AndroidNativeDecoder
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.slf4j.LoggerFactory
import org.slf4j.impl.HandroidLoggerAdapter
import xyz.gianlu.librespot.audio.decoders.Decoders
import xyz.gianlu.librespot.audio.format.SuperAudioFormat
import xyz.gianlu.librespot.player.state.DeviceStateHandler

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
    context = applicationContext
    applicationScope = CoroutineScope(SupervisorJob())
    MMKV.initialize(this, "${filesDir.absolutePath}/spa_meta")
  }

  companion object{
    lateinit var applicationScope: CoroutineScope

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
  }
}