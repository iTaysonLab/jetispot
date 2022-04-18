package bruhcollective.itaysonlab.jetispot.playback.sp

import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import androidx.annotation.FloatRange
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import okio.IOException
import xyz.gianlu.librespot.player.mixing.output.OutputAudioFormat
import xyz.gianlu.librespot.player.mixing.output.SinkException
import xyz.gianlu.librespot.player.mixing.output.SinkOutput

@RequiresApi(Build.VERSION_CODES.M)
class AndroidSinkOutput: SinkOutput {
  private var track: AudioTrack? = null
  private var lastVolume = -1F

  override fun start(format: OutputAudioFormat): Boolean {
    if (format.sampleSizeInBits != 16) throw SinkException("Unsupported SampleSize", null)
    if (format.channels < 1 || format.channels > 2) throw SinkException("Unsupported Number of Channels", null)

    val minBufferSize = AudioTrack.getMinBufferSize(
      format.sampleRate.toInt(),
      if (format.channels == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO,
      AudioFormat.ENCODING_PCM_16BIT
    )

    val audioFormat = AudioFormat.Builder()
      .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
      .setSampleRate(format.sampleRate.toInt())
      .build()

    try {
      track = AudioTrack.Builder()
        .setBufferSizeInBytes(minBufferSize)
        .setAudioFormat(audioFormat)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .build()
    } catch (e: UnsupportedOperationException) {
      throw SinkException("AudioTrack creation failed in Sink: ", e.cause)
    }

    if (lastVolume != -1F) track!!.setVolume(lastVolume)

    track!!.play()
    return true
  }

  override fun write(buffer: ByteArray, offset: Int, len: Int) {
    when (track!!.write(buffer, offset, len, AudioTrack.WRITE_BLOCKING)) {
      AudioTrack.ERROR -> throw IOException("Generic Operation Failure while writing Track")
      AudioTrack.ERROR_BAD_VALUE -> throw IOException("Invalid value used while writing Track")
      AudioTrack.ERROR_DEAD_OBJECT -> throw IOException("Track Object has died in the meantime")
      AudioTrack.ERROR_INVALID_OPERATION -> throw IOException("Failure due to improper use of Track Object methods")
    }
  }

  override fun flush() {
    track?.flush()
  }

  override fun setVolume(@FloatRange(from = 0.0, to = 1.0) volume: Float): Boolean {
    lastVolume = volume
    track?.setVolume(volume)
    return true
  }

  override fun release() {
    track?.release()
  }

  override fun stop() {
    if (track?.playState != AudioTrack.PLAYSTATE_STOPPED) track?.stop()
  }

  override fun close() {
    track = null
  }

  @VisibleForTesting
  fun getPlayState() = track?.playState
}