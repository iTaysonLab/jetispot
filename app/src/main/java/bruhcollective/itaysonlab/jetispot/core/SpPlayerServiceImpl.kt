package bruhcollective.itaysonlab.jetispot.core

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media2.common.MediaItem
import androidx.media2.common.SessionPlayer
import androidx.media2.session.MediaController
import androidx.media2.session.SessionCommandGroup
import androidx.media2.session.SessionToken
import bruhcollective.itaysonlab.jetispot.playback.helpers.MediaItemWrapper
import bruhcollective.itaysonlab.jetispot.playback.service.SpPlaybackService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SpPlayerServiceImpl (
  private val context: Context,
  private val manager: SpPlayerServiceManager
) : MediaController.ControllerCallback(), CoroutineScope by MainScope() {
  private var mediaController: MediaController? = null
  private var svcInit: (MediaController.() -> Unit)? = null
  private var timer: Job = Job().apply { cancel() }

  private val progressFlow = flow {
    while (true) {
      Log.d("PF", "${manager.playbackState.value} -> ${mediaController?.currentPosition}")

      if (manager.playbackState.value == SpPlayerServiceManager.PlaybackState.Playing) {
        emit(mediaController?.currentPosition ?: 0L)
      }

      delay(1000)
    }
  }.catch {
    emit(0L)
  }

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

  fun awaitService(init: (MediaController.() -> Unit)? = null): MediaController {
    val mc = mediaController
    if (mc != null) {
      if (!mc.isConnected) {
        Log.w("Jetispot:Service", "MC created, but not connected -> should be a deadlock, report if you see this!")
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

  override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
    manager.currentTrack.value = MediaItemWrapper(item)
  }

  override fun onPlayerStateChanged(controller: MediaController, state: Int) {
    manager.playbackState.value = when (state) {
      SessionPlayer.PLAYER_STATE_PLAYING -> SpPlayerServiceManager.PlaybackState.Playing
      SessionPlayer.PLAYER_STATE_PAUSED -> SpPlayerServiceManager.PlaybackState.Paused
      else -> SpPlayerServiceManager.PlaybackState.Idle
    }

    manageTimer(manager.playbackState.value == SpPlayerServiceManager.PlaybackState.Playing)
  }

  private fun manageTimer(run: Boolean) {
    if (run) {
      if (timer.isCancelled) {
        timer = launch {
          progressFlow.collect { p ->
            manager.playbackProgress.value = SpPlayerServiceManager.PlaybackProgress(androidx.core.util.Pair(
              if (manager.currentTrack.value.duration == 0L) 0f else (p.toFloat() / manager.currentTrack.value.duration.toFloat()), p
            ))
          }
        }
      }
    } else {
      timer.cancel()
    }
  }
}