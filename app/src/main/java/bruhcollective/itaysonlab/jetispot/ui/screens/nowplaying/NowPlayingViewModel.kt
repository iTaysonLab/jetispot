package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying

import androidx.collection.LruCache
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpPartnersApi
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.ui.monet.ColorToScheme
import bruhcollective.itaysonlab.jetispot.ui.navigation.NavigationController
import bruhcollective.itaysonlab.jetispot.ui.screens.BottomSheet
import com.spotify.metadata.Metadata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import xyz.gianlu.librespot.common.Utils
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
  private val spPlayerServiceManager: SpPlayerServiceManager,
  private val spPartnersApi: SpPartnersApi
) : ViewModel(), SpPlayerServiceManager.ServiceExtraListener, CoroutineScope by MainScope() {
  // states
  val currentTrack get() = spPlayerServiceManager.currentTrack
  val currentPosition get() = spPlayerServiceManager.playbackProgress
  val currentState get() = spPlayerServiceManager.playbackState
  val currentContext get() = spPlayerServiceManager.currentContext
  val currentContextUri get() = spPlayerServiceManager.currentContextUri
  val currentQueue get() = spPlayerServiceManager.currentQueue
  val currentQueuePosition get() = spPlayerServiceManager.currentQueuePosition
  val currentBgColor = mutableStateOf(Color.Transparent)
  var queueButtonParams by mutableStateOf(Offset.Zero)

  // TODO animate
  val currentColorScheme = mutableStateOf(lightColorScheme() to darkColorScheme())

  // ui bridges
  var uiOnTrackIndexChanged: (Int) -> Unit = {}

  // caches
  private val imageCache = LruCache<String, Pair<ColorScheme, ColorScheme>>(10)
  private var imageColorTask: Job? = null

  private fun getCurrentTrackAsMetadata() = currentQueue.value[currentQueuePosition.value]

  fun skipPrevious() = spPlayerServiceManager.skipPrevious()
  fun togglePlayPause() = spPlayerServiceManager.playPause()
  fun skipNext() = spPlayerServiceManager.skipNext()

  @OptIn(ExperimentalMaterialApi::class)
  fun navigateToSource(scope: CoroutineScope, sheetState: BottomSheetState, navigationController: NavigationController) {
    scope.launch { sheetState.collapse() }
    navigationController.navigate(currentContextUri.value)
  }

  @OptIn(ExperimentalMaterialApi::class)
  fun navigateToArtist(scope: CoroutineScope, sheetState: BottomSheetState, navigationController: NavigationController) {
    scope.launch { sheetState.collapse() }
    navigationController.navigate(
      BottomSheet.JumpToArtist, mapOf(
        "artistIdsAndRoles" to getCurrentTrackAsMetadata().artistWithRoleList.joinToString("|") { Utils.bytesToHex(it.toByteString()) }
      )
    )
  }

  init {
    spPlayerServiceManager.registerExtra(this)
  }

  override fun onCleared() {
    spPlayerServiceManager.unregisterExtra(this)
  }

  override fun onTrackIndexChanged(new: Int) {
    if (currentQueue.value.isEmpty()) return
    uiOnTrackIndexChanged.invoke(new)

    imageColorTask?.cancel()
    imageColorTask = launch(Dispatchers.IO) {
      currentColorScheme.value = calculateDominantColor(
        spPartnersApi,
        SpUtils.getImageUrl(currentQueue.value[new].album.coverGroup.imageList.find { it.size == Metadata.Image.Size.LARGE }?.fileId)
          ?: return@launch
      )
    }
  }

  fun getHeaderTitle(): Int {
    if (currentContextUri.value == "") return R.string.playing_src_unknown
    var uriSeparated = currentContextUri.value.split(":").drop(1)
    if (uriSeparated[0] == "user") uriSeparated = uriSeparated.drop(2)
    return when (uriSeparated[0]) {
      "collection" -> R.string.playing_src_library
      "playlist" -> R.string.playing_src_playlist
      "album" -> R.string.playing_src_album
      "artist" -> R.string.playing_src_artist
      else -> R.string.playing_src_unknown
    }
  }

  fun getHeaderText(): String {
    return when {
      currentContextUri.value.contains("collection") -> "Liked Songs" // TODO: to R.string
      else -> currentContext.value
    }
  }

  suspend fun calculateDominantColor(
    partnersApi: SpPartnersApi,
    url: String
  ): Pair<ColorScheme, ColorScheme> {
    return try {
      if (imageCache[url] != null) {
        return imageCache[url]!!
      }

      val apiResult = partnersApi.fetchExtractedColors(variables = "{\"uris\":[\"$url\"]}").data.extractedColors[0]

      val light = ColorToScheme.convert(android.graphics.Color.parseColor(apiResult.colorDark.hex), false)
      val dark = ColorToScheme.convert(android.graphics.Color.parseColor(apiResult.colorDark.hex), true)

      (light to dark).also { imageCache.put(url, it) }
    } catch (e: Exception) {
      lightColorScheme() to darkColorScheme()
    }
  }
}