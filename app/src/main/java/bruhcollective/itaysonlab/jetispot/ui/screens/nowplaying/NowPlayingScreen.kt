package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.ui.shared.PlayPauseButton
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableSyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun NowPlayingScreen(
  navController: NavController,
  bottomSheetState: BottomSheetState,
  bsOffset: Float,
  viewModel: NowPlayingViewModel = hiltViewModel()
) {
  Box(Modifier.fillMaxSize()) {
    Surface(tonalElevation = 4.dp, modifier = Modifier.fillMaxSize()) {
      // main content
      NowPlayingHeader(
        state = viewModel.currentContext.value, modifier = Modifier
          .align(Alignment.TopCenter)
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .statusBarsPadding()
      )
    }

    NowPlayingMiniplayer(
      viewModel,
      Modifier
        .fillMaxWidth()
        .height(72.dp)
        .align(Alignment.TopStart)
        .alpha(1f - bsOffset)
    )
  }
}

@Composable
fun NowPlayingMiniplayer(
  viewModel: NowPlayingViewModel,
  modifier: Modifier
) {
  Surface(tonalElevation = 8.dp, modifier = modifier) {
    Box(Modifier.fillMaxSize()) {
      Surface(
        Modifier
          .height(2.dp)
          .fillMaxWidth(1f), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
      ) {}
      Surface(
        Modifier
          .height(2.dp)
          .fillMaxWidth(viewModel.currentPosition.value.progressRange),
        color = MaterialTheme.colorScheme.primary
      ) {}

      Row(
        Modifier
          .fillMaxHeight()
          .padding(horizontal = 16.dp)
      ) {
        PreviewableSyncImage(
          viewModel.currentTrack.value.artworkCompose,
          placeholderType = "track",
          modifier = Modifier
            .size(48.dp)
            .align(Alignment.CenterVertically)
            .clip(RoundedCornerShape(8.dp))
        )

        Column(
          Modifier
            .weight(2f)
            .padding(horizontal = 14.dp)
            .align(Alignment.CenterVertically)
        ) {
          Text(
            viewModel.currentTrack.value.title,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp
          )
          Text(
            viewModel.currentTrack.value.artist,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
          )
        }

        PlayPauseButton(
          viewModel.currentState.value == SpPlayerServiceManager.PlaybackState.Playing,
          { viewModel.togglePlayPause() },
          MaterialTheme.colorScheme.onSurface,
          Modifier
            .fillMaxHeight()
            .width(56.dp)
            .align(Alignment.CenterVertically)
        )
      }
    }
  }
}

@Composable
fun NowPlayingHeader(
  state: String,
  modifier: Modifier
) {
  Row(modifier) {
    IconButton(onClick = { /*TODO*/ }, Modifier.size(32.dp)) {
      Icon(imageVector = Icons.Default.ArrowDownward, tint = Color.White, contentDescription = null)
    }

    Column(Modifier.weight(1f)) {
      Text(
        text = stringResource(id = R.string.np_playing_from),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        textAlign = TextAlign.Center,
        color = Color.White.copy(alpha = 0.7f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        letterSpacing = 2.sp,
      )

      Text(
        text = state,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
      )
    }

    IconButton(onClick = { /*TODO*/ }, Modifier.size(32.dp)) {
      Icon(imageVector = Icons.Default.MoreVert, tint = Color.White, contentDescription = null)
    }
  }
}

@Composable
fun NowPlayingControls(

) {

}

@Composable
fun NowPlayingBackground(

) {

}

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
  private val spPlayerServiceManager: SpPlayerServiceManager
) : ViewModel() {
  val currentTrack get() = spPlayerServiceManager.currentTrack
  val currentPosition get() = spPlayerServiceManager.playbackProgress
  val currentState get() = spPlayerServiceManager.playbackState
  val currentContext get() = spPlayerServiceManager.currentContext

  fun togglePlayPause() {
    spPlayerServiceManager.playPause()
  }
}