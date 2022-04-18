package xyz.gianlu.librespot.player.decoders.tremolo

import java.io.IOException
import java.io.InputStream

abstract class SeekableInputStream: InputStream() {
  @Throws(IOException::class)
  abstract fun seek(positionBytes: Long)

  @Throws(IOException::class)
  abstract fun tell(): Long

  @Throws(IOException::class)
  abstract fun length(): Long

  @Throws(IOException::class)
  abstract override fun read(bytes: ByteArray): Int

  @Throws(IOException::class)
  abstract override fun close()
}