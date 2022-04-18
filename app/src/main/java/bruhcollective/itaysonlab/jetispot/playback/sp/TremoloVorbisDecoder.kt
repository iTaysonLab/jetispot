package bruhcollective.itaysonlab.jetispot.playback.sp

import xyz.gianlu.librespot.player.decoders.Decoder
import xyz.gianlu.librespot.player.decoders.SeekableInputStream
import java.io.OutputStream
import xyz.gianlu.librespot.player.decoders.tremolo.OggDecodingInputStream
import xyz.gianlu.librespot.player.mixing.output.OutputAudioFormat
import java.io.IOException

class TremoloVorbisDecoder(
  audioFile: SeekableInputStream,
  normalizationFactor: Float,
  duration: Int
): Decoder(audioFile, normalizationFactor, duration) {
  private val buffer = ByteArray(2 * BUFFER_SIZE)
  private val ois: OggDecodingInputStream

  init {
    seekZero = audioIn.position()
    ois = OggDecodingInputStream(object: xyz.gianlu.librespot.player.decoders.tremolo.SeekableInputStream() {
      override fun tell() = (audioIn.position() - seekZero).toLong()
      override fun length() = ((audioIn.available() + audioIn.position()) - seekZero).toLong()
      override fun read(bytes: ByteArray) = audioIn.read(bytes)
      override fun read() = audioIn.read()

      override fun seek(positionBytes: Long) {
        audioIn.seek((positionBytes + seekZero).toInt())
      }

      override fun close() {
        audioIn.close()
      }
    })
    audioFormat = OutputAudioFormat(44100F, 16, 2, true, false)
  }

  @Throws(IOException::class)
  @Synchronized
  override fun readInternal(out: OutputStream): Int {
    if (closed) return -1

    val count = ois.read(buffer)
    if (count == -1)
      return -1

    out.write(buffer, 0, count)
    out.flush()
    return count
  }

  @Throws(CannotGetTimeException::class)
  override fun time(): Int {
    return ois.tellMs().toInt()
  }

  @Throws(IOException::class)
  override fun close() {
    ois.close()
    super.close()
  }

  override fun seek(positionMs: Int) {
    if (!closed) ois.seekMs(positionMs)
  }
}