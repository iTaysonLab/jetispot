package bruhcollective.itaysonlab.jetispot.playback.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.media3.common.Player
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionResult
import bruhcollective.itaysonlab.jetispot.MainActivity
import bruhcollective.itaysonlab.jetispot.core.SpPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("UnsafeOptInUsageError")
class SpPlaybackService : MediaLibraryService(), CoroutineScope by CoroutineScope(Dispatchers.Main + SupervisorJob()) {
  @Inject
  lateinit var spPlayerManager: SpPlayerManager

  private lateinit var mediaLibrarySession: MediaLibrarySession
  private lateinit var playerWrapper: SpPlayerWrapper

  private val librarySessionCallback = SessionCallback()

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaLibrarySession

  override fun onCreate() {
    super.onCreate()
    createSession()
  }

  override fun onDestroy() {
    playerWrapper.release()
    mediaLibrarySession.release()
    super.onDestroy()
  }

  private fun createSession() {
    playerWrapper = SpPlayerWrapper(this)

    playerWrapper.runOnPlayback {
      spPlayerManager.player().addEventsListener(SpServiceEventsListener(playerWrapper))
      spPlayerManager.player().waitReady()
      playerWrapper.state.playbackState = Player.STATE_READY
      playerWrapper.runOnListeners {
        it.onPlaybackStateChanged(Player.STATE_READY)
        it.onTimelineChanged(playerWrapper.currentTimeline, Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED)
      }
    }

    mediaLibrarySession =
      MediaLibrarySession.Builder(this, playerWrapper, librarySessionCallback).apply {
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

  inner class SessionCallback : MediaLibrarySession.MediaLibrarySessionCallback {
    override fun onSetMediaUri(
      session: MediaSession,
      controller: MediaSession.ControllerInfo,
      uri: Uri,
      extras: Bundle
    ): Int {
      playerWrapper.runOnPlayback {
        spPlayerManager.player().load(uri.toString(), true, extras.getBoolean("spShuffle", false))
      }

      return SessionResult.RESULT_SUCCESS
    }
  }
}