package bruhcollective.itaysonlab.jetispot.core

import android.os.Looper
import bruhcollectie.itaysonlab.jetispot.proto.AudioNormalization
import bruhcollective.itaysonlab.jetispot.playback.service.refl.SpReflect
import bruhcollective.itaysonlab.jetispot.playback.sp.AndroidSinkOutput
import xyz.gianlu.librespot.audio.decoders.AudioQuality
import xyz.gianlu.librespot.player.Player
import xyz.gianlu.librespot.player.PlayerConfiguration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpPlayerManager @Inject constructor(
  private val sessionManager: SpSessionManager,
  private val configurationManager: SpConfigurationManager
) {
  @Volatile
  private var _player: Player? = null
  private var _playerReflect: SpReflect? = null

  fun reflect(): SpReflect {
    _player ?: error("Player not yet created!")

    if (_playerReflect == null) {
      _playerReflect = SpReflect { _player!! }
    }

    return _playerReflect!!
  }

  fun isPlayerAvailable () = _player != null
  fun playerNullable() = _player

  fun createPlayer() {
    if (_player != null) return
    _player = verifyNotMainThread {
      Player(PlayerConfiguration.Builder().apply {
        setOutput(PlayerConfiguration.AudioOutput.CUSTOM)
        setOutputClass(AndroidSinkOutput::class.java.name)

        val config = configurationManager.syncPlayerConfig()
        setCrossfadeDuration(config.crossfade)
        setEnableNormalisation(config.normalization)
        setAutoplayEnabled(config.autoplay)
        setPreloadEnabled(config.preload)

        setPreferredQuality(when (config.preferredQuality) {
          bruhcollectie.itaysonlab.jetispot.proto.AudioQuality.NORMAL -> AudioQuality.NORMAL
          bruhcollectie.itaysonlab.jetispot.proto.AudioQuality.HIGH -> AudioQuality.HIGH
          bruhcollectie.itaysonlab.jetispot.proto.AudioQuality.VERY_HIGH -> AudioQuality.VERY_HIGH
          else -> AudioQuality.VERY_HIGH
        })

        setNormalisationPregain(when (config.normalizationLevel) {
          AudioNormalization.QUIET -> -5f
          AudioNormalization.BALANCED -> 3f
          AudioNormalization.LOUD -> 6f
          else -> 3f
        })

        //setVolumeSteps(100)
        //setInitialVolume(100)
      }.build(), sessionManager.session)
    }
  }

  fun player(): Player {
    return _player ?: error("Player not yet created!")
  }

  fun release () = verifyNotMainThread {
    _player?.close()
    _player = null
    _playerReflect = null
  }

  //

  private fun <T> verifyNotMainThread (block: () -> T): T {
    if (Looper.getMainLooper() == Looper.myLooper()) throw IllegalStateException("This should be run only on the non-UI thread!")
    return block()
  }
}