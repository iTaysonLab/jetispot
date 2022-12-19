package bruhcollective.itaysonlab.jetispot.core

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.FloatRange
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.util.Pair
import bruhcollective.itaysonlab.jetispot.core.lyrics.SpLyricsController
import bruhcollective.itaysonlab.jetispot.core.lyrics.SpLyricsRequester
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextPlayerData
import bruhcollective.itaysonlab.jetispot.playback.helpers.MediaItemWrapper
import com.google.common.util.concurrent.ListenableFuture
import com.spotify.metadata.Metadata
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@OptIn(ExperimentalStdlibApi::class)
@Singleton
class SpPlayerServiceManager @Inject constructor(
  @ApplicationContext private val context: Context,
  private val moshi: Moshi,
  val sessionManager: SpSessionManager,
  val metadataRequester: SpMetadataRequester
) {
  private val impl = SpPlayerServiceImpl(context, this)
  private var extraListeners = mutableListOf<ServiceExtraListener>()

  // states
  val currentTrack = mutableStateOf(MediaItemWrapper())
  val playbackState = mutableStateOf(PlaybackState.Idle)
  val playbackProgress = mutableStateOf(PlaybackProgress(Pair(0F, 0L)))
  val currentContext = mutableStateOf("")
  val currentContextUri = mutableStateOf("")
  val currentQueue = mutableStateOf<List<Metadata.Track>>(emptyList())
  val currentQueuePosition = mutableStateOf(0)

  @JvmInline
  value class PlaybackProgress(
    private val pairData: Pair<Float, Long>
  ) {
    val progressRange: Float
    @FloatRange(from = 0.0, to = 1.0) get() = pairData.first.coerceIn(0f..1f)

    val progressMilliseconds: Long
    get() = pairData.second
  }

  fun reset() {
    currentTrack.value = MediaItemWrapper()
    playbackState.value = PlaybackState.Idle
  }
  //

  // Uri should be spotify:<track/album/..>:<id>
  fun play(data: PlayFromContextData) = play(data.uri, data.player)

  fun play(uri: String, data: PlayFromContextPlayerData) = impl.awaitService {
    Log.i("SpPlayerServiceManager", "Trying to play audio with $uri as URI and $data as data")
    setMediaUri(uri.toUri(), Bundle().also { it.putString("sp_json", moshi.adapter<PlayFromContextPlayerData>().toJson(data)) })
  }

  fun playPause() = impl.awaitService {
    if (playbackState.value == PlaybackState.Playing) {
      this.pause()
    } else {
      this.play()
    }
  }

  fun skipNext() = impl.awaitService { this.skipToNextPlaylistItem() }
  fun skipPrevious() = impl.awaitService { this.skipToPreviousPlaylistItem() }
  fun seekTo(ms: Long) = impl.awaitService { this.seekTo(ms) }

  enum class PlaybackState {
    Idle, Playing, Paused
  }

  // for extra UI updates
  interface ServiceExtraListener {
    fun onTrackIndexChanged(new: Int)
    fun onTrackProgressChanged(pos: Long)
  }

  fun registerExtra(l: ServiceExtraListener) { extraListeners.add(l) }
  fun unregisterExtra(l: ServiceExtraListener) { extraListeners.remove(l) }
  fun runExtra(func: (ServiceExtraListener) -> Unit) { extraListeners.forEach(func) }
}