package bruhcollective.itaysonlab.jetispot.playback.sp

import xyz.gianlu.librespot.player.decoders.Decoder
import xyz.gianlu.librespot.player.decoders.SeekableInputStream
import java.io.OutputStream

class TremoloVorbisDecoder(
  private val audioFile: SeekableInputStream,
  private val normalizationFactor: Float,
  private val duration: Int
): Decoder(audioFile, normalizationFactor, duration) {
  override fun readInternal(out: OutputStream): Int {
    TODO("Not yet implemented")
  }

  override fun time(): Int {
    TODO("Not yet implemented")
  }
}