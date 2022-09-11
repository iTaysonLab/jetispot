package bruhcollective.itaysonlab.jetispot.playback.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.session.*
import bruhcollective.itaysonlab.jetispot.MainActivity
import bruhcollective.itaysonlab.jetispot.core.SpPlayerManager
import bruhcollective.itaysonlab.jetispot.core.util.Log
import bruhcollective.itaysonlab.jetispot.playback.service.library.MediaLibraryConnector
import bruhcollective.itaysonlab.jetispot.playback.service.library.SessionControllerVerifier
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.guava.future
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("UnsafeOptInUsageError")
class SpPlaybackService : MediaLibraryService(), CoroutineScope by CoroutineScope(Dispatchers.Main + SupervisorJob()) {
  @Inject lateinit var spPlayerManager: SpPlayerManager
  @Inject lateinit var audioFocusManager: AudioFocusManager
  @Inject lateinit var sessionControllerVerifier: SessionControllerVerifier
  @Inject lateinit var mediaLibraryConnector: MediaLibraryConnector

  private lateinit var mediaLibrarySession: MediaLibrarySession
  private lateinit var playerWrapper: SpPlayerWrapper

  private val librarySessionCallback = SessionCallback()

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
    return if (sessionControllerVerifier.verifyController(controllerInfo)) {
      mediaLibrarySession
    } else {
      null
    }
  }

  override fun onCreate() {
    super.onCreate()
    createSession()
  }

  override fun onDestroy() {
    audioFocusManager.abandonFocus()
    mediaLibrarySession.release()
    super.onDestroy()
  }

  private fun createSession() {
    audioFocusManager.requestFocus()
    playerWrapper = SpPlayerWrapper(this)

    playerWrapper.runOnPlayback {
      spPlayerManager.player().addEventsListener(SpServiceEventsListener(playerWrapper))
      spPlayerManager.player().waitReady()
    }

    mediaLibrarySession =
      MediaLibrarySession.Builder(this, playerWrapper, librarySessionCallback)
        .setSessionActivity(PendingIntent.getActivity(
          this@SpPlaybackService,
          100,
          Intent(this@SpPlaybackService, MainActivity::class.java).apply {
            putExtra("openPlayer", true)
          },
          (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0) or PendingIntent.FLAG_UPDATE_CURRENT
        ))
        .build()
  }

  inner class SessionCallback : MediaLibrarySession.Callback {
    override fun onConnect(
      session: MediaSession,
      controller: MediaSession.ControllerInfo
    ): SessionCommandGroup {
      return SessionCommandGroup.Builder()
        .addAllPredefinedCommands(SessionCommand.COMMAND_VERSION_CURRENT)
        .addCommand(SpSessionCommands.Repeat)
        .addCommand(SpSessionCommands.Shuffle)
        .build()
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
      super.onPostConnect(session, controller)
    }

    override fun onGetLibraryRoot(
      session: MediaLibrarySession,
      controller: MediaSession.ControllerInfo,
      params: LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> = future {
      mediaLibraryConnector.root(controller, params)
    }

    override fun onGetChildren(
      session: MediaLibrarySession,
      controller: MediaSession.ControllerInfo,
      parentId: String,
      page: Int,
      pageSize: Int,
      params: LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> = future {
      mediaLibraryConnector.load(this@SpPlaybackService, parentId, page, pageSize)
    }

    override fun onCustomCommand(
      session: MediaSession,
      controller: MediaSession.ControllerInfo,
      customCommand: SessionCommand,
      args: Bundle
    ): ListenableFuture<SessionResult> {
      return super.onCustomCommand(session, controller, customCommand, args)
    }

    override fun onAddMediaItems(
      mediaSession: MediaSession,
      controller: MediaSession.ControllerInfo,
      mediaItems: List<MediaItem>
    ): ListenableFuture<List<MediaItem>> = Futures.immediateFuture(mediaItems) // TODO: we might resolve all needed data here

    override fun onSetMediaUri(
      session: MediaSession,
      controller: MediaSession.ControllerInfo,
      uri: Uri,
      extras: Bundle?
    ): Int {
      Log.d("SpPlaybackService","onSetMediaUri $controller $uri $extras")

      playerWrapper.runOnPlayback {
        val playData = extras?.getString("sp_json", null)

        if (playData != null) {
          spPlayerManager.reflect().playUsingData(playData)
        } else if (uri.toString().startsWith("spotify:")) {
          spPlayerManager.player().load(uri.toString(), true, false)
        }
      }

      return SessionResult.RESULT_SUCCESS
    }
  }
}