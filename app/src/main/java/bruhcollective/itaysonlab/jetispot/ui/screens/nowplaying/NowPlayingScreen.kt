package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.playback.helpers.MediaItemWrapper
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun NowPlayingScreen (
  navController: NavController,
  bottomSheetState: BottomSheetState,
  viewModel: NowPlayingViewModel = hiltViewModel()
) {
  Box(Modifier.fillMaxSize()) {
    NowPlayingMiniplayer(viewModel.currentTrack.value,
      Modifier
        .fillMaxWidth()
        .align(Alignment.TopStart)
        .alpha(
          1f
        ))

    Surface(tonalElevation = 16.dp, modifier = Modifier.fillMaxSize()) {
      // main content
    }
  }
}

@Composable
fun NowPlayingMiniplayer (
  currentTrack: MediaItemWrapper,
  modifier: Modifier
) {
  Surface(tonalElevation = 16.dp, modifier = modifier) {
    Row {
      //PreviewableAsyncImage(imageUrl = , placeholderType = , modifier = )
    }
  }
}

@Composable
fun NowPlayingHeader (

) {

}

@Composable
fun NowPlayingControls (

) {

}

@Composable
fun NowPlayingBackground (

) {

}

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
  private val spPlayerServiceManager: SpPlayerServiceManager
): ViewModel() {
  val currentTrack get() = spPlayerServiceManager.currentTrack
}

// Previews

@Preview
@Composable
fun NowPlayingMiniplayerPreview () {
  NowPlayingMiniplayer(
    MediaItemWrapper(),
    Modifier.fillMaxWidth()
  )
}