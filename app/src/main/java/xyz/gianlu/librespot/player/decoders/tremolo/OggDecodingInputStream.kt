package xyz.gianlu.librespot.player.decoders.tremolo

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.math.min

class OggDecodingInputStream(
  private val oggInputStream: SeekableInputStream
): InputStream() {
  private val jniBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE)
  private val handle = initDecoder(jniBuffer)

  companion object {
    private const val BUFFER_SIZE = 4096
    private const val SEEK_SET = 0
    private const val SEEK_CUR = 1
    private const val SEEK_END = 2
    private val TAG = OggDecodingInputStream::class.simpleName
  }

  init {
    System.loadLibrary("tremolo")
    if (handle == 0L) throw IOException("Couldn't start decoder!")
  }

  private external fun initDecoder(jniBuffer: ByteBuffer): Long

  private external fun read(handle: Long, len: Int): Int

  private external fun seekMs(handle: Long, milliseconds: Int): Int

  private external fun seekSamples(handle: Long, samples: Int): Int

  private external fun tellMs(handle: Long): Long

  private external fun tellSamples(handle: Long): Long

  private external fun totalSamples(handle: Long): Long

  private external fun close(handle: Long)

  private fun writeOgg(size: Int): Int {
    val bytes = ByteArray(min(size, BUFFER_SIZE))
    try {
      val read = oggInputStream.read(bytes)
      if (read > -1) {
        jniBuffer.put(bytes)
        jniBuffer.flip()
        return read
      }

      return 0
    } catch (ex: Exception) {
      Log.e(TAG, "Internal writeOgg failed.", ex)
      return -1
    }
  }

  private fun seekOgg(offset: Long, whence: Int): Int {
    return try {
      when (whence) {
        SEEK_SET -> oggInputStream.seek(offset)
        SEEK_CUR -> oggInputStream.seek(oggInputStream.tell() + offset)
        SEEK_END -> oggInputStream.seek(oggInputStream.length() + offset)
      }

      0
    } catch (ex: Exception) {
      Log.e(TAG, "Internal seekOgg failed.", ex)
      -1
    }
  }

  private fun tellOgg(): Int {
    return try {
      oggInputStream.tell().toInt()
    } catch (ex: Exception) {
      Log.e(TAG, "Internal tellOgg failed.", ex)
      -1
    }
  }

  @Synchronized
  override fun read(): Int {
    jniBuffer.clear()

    val size = read(handle, 1)
    jniBuffer.limit(size)

    val b = jniBuffer.get()
    jniBuffer.clear()
    return b.toInt()
  }

  @Synchronized
  override fun read(b: ByteArray, off: Int, len: Int): Int {
    val lenMin = min(len, BUFFER_SIZE)
    jniBuffer.clear()

    val size = read(handle, lenMin)
    if (size > 0) {
      jniBuffer.limit(size)
      jniBuffer.get(b, off, size)
      jniBuffer.clear()
      return size
    }

    return -1
  }

  @Synchronized
  override fun read(b: ByteArray): Int {
    return this.read(b, 0, b.size)
  }

  @Synchronized
  fun seekMs(milliseconds: Int): Int {
    return seekMs(handle, milliseconds)
  }

  @Synchronized
  fun tellMs(): Long {
    return tellMs(handle)
  }

  @Throws(IOException::class)
  @Synchronized
  override fun close() {
    close(handle)
    oggInputStream.close()
    super.close()
  }
}