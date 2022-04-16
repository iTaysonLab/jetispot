package bruhcollective.itaysonlab.jetispot.core

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import bruhcollective.itaysonlab.jetispot.playback.service.SpPlaybackService
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SpPlayerServiceImpl (
  private val context: Context
) : MediaController.Listener {
  private val controlsThread = HandlerThread("SpPlayer:Controls").also { it.start() }
  private val controlsHandler = Handler(controlsThread.looper)
  private val controlsScope = CoroutineScope(controlsHandler.asCoroutineDispatcher() + SupervisorJob())

  private var mediaController: MediaController? = null

  fun asyncService (func: suspend (MediaController) -> Unit) {
    controlsScope.launch { func(awaitService()) }
  }

  private suspend fun awaitService(): MediaController {
    if (mediaController != null) {
      return mediaController!!
    }

    return MediaController.Builder(context, SessionToken(context, ComponentName(context, SpPlaybackService::class.java)))
      .setListener(this)
      .buildAsync().waitForFuture().also { mediaController = it }
  }

  private suspend fun <V> ListenableFuture<V>.waitForFuture(): V {
    return suspendCancellableCoroutine { continuation ->
      this.addListener({
        if (this.isDone) continuation.resume(this.get())
      }, ContextCompat.getMainExecutor(context))
    }
  }

  override fun onDisconnected(controller: MediaController) {
    mediaController = null
  }
}