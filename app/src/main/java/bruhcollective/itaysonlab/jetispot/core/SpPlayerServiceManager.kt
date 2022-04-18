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
    setMediaUri(uri.toUri(), Bundle())
  }
}