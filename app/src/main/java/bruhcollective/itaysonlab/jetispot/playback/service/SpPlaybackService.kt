package bruhcollective.itaysonlab.jetispot.playback.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.media2.session.MediaLibraryService
import androidx.media2.session.MediaSession
import androidx.media2.session.SessionResult
import bruhcollective.itaysonlab.jetispot.MainActivity
import bruhcollective.itaysonlab.jetispot.core.SpPlayerManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("UnsafeOptInUsageError")
class SpPlaybackService : MediaLibraryService(), CoroutineScope by CoroutineScope(Dispatchers.Main + SupervisorJob()) {
  @Inject lateinit var spPlayerManager: SpPlayerManager
  @Inject lateinit var audioFocusManager: AudioFocusManager

  private lateinit var mediaLibrarySession: MediaLibrarySession
  private lateinit var playerWrapper: SpPlayerWrapper

  private val librarySessionCallback = SessionCallback()

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaLibrarySession

  override fun onCreate() {
    super.onCreate()
    createSession()
  }

  override fun onDestroy() {
    audioFocusManager.abandonFocus()
    mediaLibrarySession.close()
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
      MediaLibrarySession.Builder(this, playerWrapper, playerWrapper.playbackExecutor, librarySessionCallback).apply {
        setSessionActivity(
          PendingIntent.getActivity(
            this@SpPlaybackService,
            100,
            Intent(this@SpPlaybackService, MainActivity::class.java).apply {
              putExtra("openPlayer", true)
            },
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0) or PendingIntent.FLAG_UPDATE_CURRENT
          )
        )
      }.build()
  }

  inner class SessionCallback : MediaLibrarySession.MediaLibrarySessionCallback() {
    override fun onSetMediaUri(
      session: MediaSession,
      controller: MediaSession.ControllerInfo,
      uri: Uri,
      extras: Bundle?
    ): Int {
      playerWrapper.runOnPlayback {
        val playData = extras?.getString("sp_json", null)

        if (playData != null) {
          spPlayerManager.reflect().playUsingData(playData)
        } else {
          spPlayerManager.player().load(uri.toString(), true, false)
        }
      }

      return SessionResult.RESULT_SUCCESS
    }
  }
}