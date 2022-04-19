package bruhcollective.itaysonlab.jetispot.core

import android.content.Context
import android.os.Bundle
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpPlayerServiceManager @Inject constructor(
  @ApplicationContext private val context: Context,
) {
  private val impl = SpPlayerServiceImpl(context)

  // Uri should be spotify:<track/album/..>:<id>
  fun playFromUri (uri: String) = impl.awaitService {
    setMediaUri(uri.toUri(), SpStartPlaybackParams(null, true, false).asBundle())
  }

  class SpStartPlaybackParams(
    val skipToUri: String? = null,
    val shouldPlay: Boolean = true,
    val shouldShuffle: Boolean = false
  ) {
    constructor(bundle: Bundle?): this(
      bundle?.getString("sp_skipUri", null),
      bundle?.getBoolean("sp_play", false) ?: true,
      bundle?.getBoolean("sp_shuffle", true) ?: false,
    )

    fun asBundle() = Bundle().apply {
      putString("sp_skipUri", skipToUri)
      putBoolean("sp_play", shouldPlay)
      putBoolean("sp_shuffle", shouldShuffle)
    }
  }
}