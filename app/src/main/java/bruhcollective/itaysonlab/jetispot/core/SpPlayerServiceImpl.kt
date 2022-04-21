package bruhcollective.itaysonlab.jetispot.core

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media2.common.MediaItem
import androidx.media2.session.MediaController
import androidx.media2.session.SessionCommandGroup
import androidx.media2.session.SessionToken
import bruhcollective.itaysonlab.jetispot.playback.service.SpPlaybackService
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SpPlayerServiceImpl (
  private val context: Context,
  private val manager: SpPlayerServiceManager
) : MediaController.ControllerCallback() {
  override fun onConnected(controller: MediaController, allowedCommands: SessionCommandGroup) {
    svcInit?.let {
      controller.it()
      svcInit = null
    }
  }

  override fun onDisconnected(controller: MediaController) {
    mediaController = null
    manager.reset()
  }

  private var mediaController: MediaController? = null
  private var svcInit: (MediaController.() -> Unit)? = null

  fun awaitService(init: (MediaController.() -> Unit)? = null): MediaController {
    val mc = mediaController
    if (mc != null) {
      if (!mc.isConnected) {
        svcInit = init
      } else {
        mc.also {
          init?.invoke(it)
        }
      }
      return mc
    } else {
      svcInit = init
      mediaController = MediaController.Builder(context)
        .setSessionToken(SessionToken(context, ComponentName(context, SpPlaybackService::class.java)))
        .setControllerCallback(ContextCompat.getMainExecutor(context), this)
        .build()
      return mediaController!!
    }
  }

  private suspend fun <V> ListenableFuture<V>.waitForFuture(): V {
    return suspendCancellableCoroutine { continuation ->
      this.addListener({
        if (this.isDone) continuation.resume(this.get())
      }, ContextCompat.getMainExecutor(context))
    }
  }

  override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
    manager.currentTrack.value = item
  }
}