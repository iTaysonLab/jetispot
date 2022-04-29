package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.playback.helpers.MediaItemWrapper
import bruhcollective.itaysonlab.jetispot.ui.shared.PlayPauseButton
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableSyncImage
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
    Surface(tonalElevation = 4.dp, modifier = Modifier.fillMaxSize()) {
      // main content
    }

    NowPlayingMiniplayer(viewModel.currentTrack.value,
      Modifier
        .fillMaxWidth()
        .height(72.dp)
        .align(Alignment.TopStart)
        .alpha(1f))
  }
}

@Composable
fun NowPlayingMiniplayer (
  currentTrack: MediaItemWrapper,
  modifier: Modifier
) {
  Surface(tonalElevation = 8.dp, modifier = modifier) {
    Box(Modifier.fillMaxSize()) {
      Surface(
        Modifier
          .height(2.dp)
          .fillMaxWidth(1f), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)) {}
      Surface(
        Modifier
          .height(2.dp)
          .fillMaxWidth(0.4f), color = MaterialTheme.colorScheme.primary) {}

      Row(
        Modifier
          .fillMaxHeight()
          .padding(horizontal = 16.dp)) {
        PreviewableSyncImage(currentTrack.artworkCompose, placeholderType = "track", modifier = Modifier
          .size(48.dp)
          .align(Alignment.CenterVertically)
          .clip(RoundedCornerShape(8.dp)))

        Column(
          Modifier
            .weight(2f)
            .padding(horizontal = 14.dp)
            .align(Alignment.CenterVertically)) {
          Text(currentTrack.title, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.sp)
          Text(currentTrack.artist, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        PlayPauseButton(
          MaterialTheme.colorScheme.onSurface,
          Modifier.fillMaxHeight().width(56.dp).align(Alignment.CenterVertically)
        )
      }
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