package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PlayPauseButton
import bruhcollective.itaysonlab.jetispot.ui.shared.navClickable
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NowPlayingControls(
  scope: CoroutineScope,
  bottomSheetState: BottomSheetState,
  viewModel: NowPlayingViewModel,
  modifier: Modifier
) {
  Column(modifier, verticalArrangement = Arrangement.Bottom) {
    ControlsHeader(scope, bottomSheetState, viewModel)
    Spacer(Modifier.height(8.dp))
    ControlsSeekbar(viewModel)
    Spacer(Modifier.height(16.dp))
    ControlsMainButtons(viewModel)
    Spacer(Modifier.height(16.dp))
    ControlsBottomAccessories(viewModel)
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ControlsHeader(
  scope: CoroutineScope,
  bottomSheetState: BottomSheetState,
  viewModel: NowPlayingViewModel,
) {
  MediumText(text = viewModel.currentTrack.value.title, modifier = Modifier.padding(horizontal = 14.dp).navClickable(
    enableRipple = false
  ) { navController ->
    viewModel.navigateToSource(scope, bottomSheetState, navController)
  }, fontSize = 24.sp, color = Color.White,)
  Spacer(Modifier.height(2.dp))
  Text(text = viewModel.currentTrack.value.artist, modifier = Modifier.padding(horizontal = 14.dp).navClickable(
    enableRipple = false
  ) { navController ->
    viewModel.navigateToArtist(scope, bottomSheetState, navController)
  }, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f))
}

@Composable
private fun ControlsSeekbar(
  viewModel: NowPlayingViewModel,
) {
  Slider(value = viewModel.currentPosition.value.progressRange, colors = SliderDefaults.colors(
    thumbColor = Color.White,
    activeTrackColor = Color.White,
    inactiveTrackColor = Color.White.copy(alpha = 0.5f)
  ), onValueChange = {}, modifier = Modifier.padding(horizontal = 8.dp))

  Row(Modifier.padding(horizontal = 14.dp).offset(y = (-6).dp)) {
    Text(text = DateUtils.formatElapsedTime(viewModel.currentPosition.value.progressMilliseconds / 1000L), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    Spacer(modifier = Modifier.weight(1f))
    Text(text = DateUtils.formatElapsedTime(viewModel.currentTrack.value.duration / 1000L), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
  }
}

@Composable
private fun ControlsMainButtons(
  viewModel: NowPlayingViewModel,
) {
  Row {
    IconButton(
      onClick = { /*TODO*/ },
      modifier = Modifier.size(56.dp),
      colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
    ) {
      Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = null)
    }

    Spacer(modifier = Modifier.weight(1f))

    IconButton(
      onClick = { viewModel.skipPrevious() },
      modifier = Modifier.size(56.dp),
      colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
    ) {
      Icon(imageVector = Icons.Rounded.SkipPrevious, contentDescription = null)
    }

    Spacer(modifier = Modifier.width(24.dp))

    Surface(color = Color.White, modifier = Modifier.clip(CircleShape).clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = rememberRipple(color = Color.Black)
    ) {
      viewModel.togglePlayPause()
    }) {
      PlayPauseButton(
        isPlaying = viewModel.currentState.value == SpPlayerServiceManager.PlaybackState.Playing,
        color = if (viewModel.currentBgColor.value != Color.Transparent) viewModel.currentBgColor.value else Color.Black,
        modifier = Modifier.size(56.dp).align(Alignment.CenterVertically)
      )
    }

    Spacer(modifier = Modifier.width(24.dp))

    IconButton(
      onClick = { viewModel.skipNext() },
      modifier = Modifier.size(56.dp),
      colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
    ) {
      Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = null)
    }

    Spacer(modifier = Modifier.weight(1f))

    IconButton(
      onClick = { /*TODO*/ },
      modifier = Modifier.size(56.dp),
      colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
    ) {
      Icon(imageVector = Icons.Rounded.Repeat, contentDescription = null)
    }
  }
}

@Composable
private fun ControlsBottomAccessories(
  viewModel: NowPlayingViewModel,
) {
  Row {
    IconButton(
      onClick = { /*TODO*/ },
      modifier = Modifier.size(56.dp),
      colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
    ) {
      Icon(imageVector = Icons.Rounded.Share, contentDescription = null)
    }

    Spacer(modifier = Modifier.weight(1f))

    IconButton(
      onClick = { /*TODO*/ },
      modifier = Modifier.size(56.dp),
      colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
    ) {
      Icon(imageVector = Icons.Rounded.QueueMusic, contentDescription = null)
    }
  }
}