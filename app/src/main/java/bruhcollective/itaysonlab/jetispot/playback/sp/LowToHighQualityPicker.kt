package bruhcollective.itaysonlab.jetispot.playback.sp

import android.util.Log
import com.spotify.metadata.Metadata
import xyz.gianlu.librespot.audio.decoders.AudioQuality
import xyz.gianlu.librespot.audio.decoders.VorbisOnlyAudioQuality
import xyz.gianlu.librespot.audio.format.AudioQualityPicker
import xyz.gianlu.librespot.audio.format.SuperAudioFormat

/**
 * key features:
 * - support not only vorbis (Android also plays MP3)
 * - adaptive quality: don't use the first suitable file if quality is not found
 */
class LowToHighQualityPicker(
  private val preferredQualityProvider: () -> AudioQuality
): AudioQualityPicker {
  companion object {
    private val ORDER = arrayOf(AudioQuality.LOW, AudioQuality.NORMAL, AudioQuality.HIGH, AudioQuality.VERY_HIGH, AudioQuality.FLAC)
    private val SUPPORTED_FORMATS = listOf(SuperAudioFormat.VORBIS, SuperAudioFormat.MP3)
  }

  private fun List<Metadata.AudioFile>.filterSupported() = filter { it.hasFileId() && it.hasFormat() }.filter {
    SUPPORTED_FORMATS.contains(SuperAudioFormat.get(it.format))
  }

  override fun getFile(files: MutableList<Metadata.AudioFile>): Metadata.AudioFile? {
    Log.d("LtHQP", "available quality: ${files.joinToString { it.format.name }}, preferred: ${preferredQualityProvider().name}")
    val preferredFiles = preferredQualityProvider().getMatches(files).filterSupported()
    if (preferredFiles.isEmpty()) {
      Log.d("LtHQP", "=> not found, searching for other")
      ORDER.forEach { aq ->
        val ordered = aq.getMatches(files).filterSupported()
        if (ordered.isNotEmpty()) {
          Log.d("LtHQP", "=> [${aq.name}] found: ${ordered.joinToString { it.format.name }}")
          return ordered[0]
        }
      }
      Log.d("LtHQP", "=> still not found")
      return null
    } else {
      Log.d("LtHQP", "=> found: ${preferredFiles.joinToString { it.format.name }}")
      return preferredFiles[0]
    }
  }
}