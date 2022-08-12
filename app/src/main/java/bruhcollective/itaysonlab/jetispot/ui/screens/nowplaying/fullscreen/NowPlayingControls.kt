package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.ui.ext.blendWith
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.MarqueeText
import bruhcollective.itaysonlab.jetispot.ui.shared.PlayPauseButton
import bruhcollective.itaysonlab.jetispot.ui.shared.navClickable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.CoroutineScope
import androidx.compose.material3.MaterialTheme.colorScheme as monet


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ControlsHeader(
  scope: CoroutineScope,
  bottomSheetState: BottomSheetState,
  viewModel: NowPlayingViewModel
) {
  Row(horizontalArrangement = Arrangement.SpaceBetween) {
    Column(modifier = Modifier.weight(0.9f)) {
      MarqueeText(
        text = viewModel.currentTrack.value.title,
        modifier = Modifier
          .padding(horizontal = 14.dp)
          .navClickable{ viewModel.navigateToSource(scope, bottomSheetState, it) },
        fontSize = 24.sp,
        color = monet.onSecondaryContainer.copy(0.85f),
        fontWeight = FontWeight.ExtraBold,
        basicGradientColor = if (isSystemInDarkTheme())
          MaterialTheme.colorScheme.surface.blendWith(monet.primary, ratio = 0.05f)
        else
          MaterialTheme.colorScheme.surface.blendWith(monet.primary, ratio = 0.1f)
      )

      Spacer(Modifier.height(2.dp))

      MarqueeText(
        text = viewModel.currentTrack.value.artist,
        modifier = Modifier
          .padding(horizontal = 14.dp)
          .navClickable { viewModel.navigateToArtist(scope, bottomSheetState, it) },
        overflow = TextOverflow.Ellipsis,
        fontSize = 18.sp,
        color = monet.onSecondaryContainer.copy(alpha = 0.7f),
        basicGradientColor = if (isSystemInDarkTheme())
          MaterialTheme.colorScheme.surface.blendWith(monet.primary, ratio = 0.05f)
        else
          MaterialTheme.colorScheme.surface.blendWith(monet.primary, ratio = 0.1f)
      )
    }

    Box(modifier = Modifier
      .weight(0.1f)
      .align(Alignment.CenterVertically)
    ) {
      Icon(
        imageVector = Icons.Rounded.Favorite,
        contentDescription = "",
        tint = monet.onSecondaryContainer.copy(0.85f),
        modifier = Modifier
          .padding(end = 12.dp)
          .size(26.dp)
      )
    }
  }
}

@Composable
fun ControlsSeekbar(viewModel: NowPlayingViewModel) {
  Box {
    Slider(
      value = viewModel.currentPosition.value.progressRange,
      colors = SliderDefaults.colors(
        thumbColor = monet.onSecondaryContainer,
        activeTrackColor = monet.onSecondaryContainer.copy(0.85f),
        inactiveTrackColor = monet.onSecondaryContainer.copy(alpha = 0.2f)
      ),
      onValueChange = {},
      modifier = Modifier.padding(horizontal = 6.dp)
    )

    Column(
      modifier = Modifier
        .padding(horizontal = 13.dp)
        .fillMaxWidth(),
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.Bottom
    ) {
      Row(Modifier.height(52.dp), verticalAlignment = Alignment.Bottom) {
        Text(
          text = viewModel.currentPosition.value.progressFmt,
          color = monet.onSecondaryContainer.copy(0.85f),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )

        Text(text = " / ", color = monet.onSecondaryContainer.copy(0.85f), fontSize = 12.sp)

        Text(
          text = DateUtils.formatElapsedTime(viewModel.currentTrack.value.duration / 1000L),
          color = monet.onSecondaryContainer.copy(0.85f),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun ControlsMainButtons(
  viewModel: NowPlayingViewModel,
  queueOpened: Boolean,
  setQueueOpened: (Boolean) -> Unit
) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
  ) {
    IconButton(
      onClick = { /*TODO*/ },
      modifier = Modifier.size(32.dp),
      colors = IconButtonDefaults.iconButtonColors(
        contentColor = monet.onSecondaryContainer.copy(0.85f)
      )
    ) {
      Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = null)
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier.fillMaxWidth(0.85f)
    ) {
      IconButton(
        onClick = { viewModel.skipPrevious() },
        modifier = Modifier
          .size(56.dp)
          .clip(RoundedCornerShape(28.dp))
          .background(monet.onPrimaryContainer.copy(0.1f)),
        colors = IconButtonDefaults.iconButtonColors(
          contentColor = monet.onSecondaryContainer.copy(0.85f)
        )
      ) {
        Icon(
          imageVector = Icons.Rounded.SkipPrevious,
          contentDescription = null,
          modifier = Modifier.size(42.dp)
        )
      }

      Surface(
        color = monet.primaryContainer.blendWith(monet.primary, 0.3f).copy(0.5f),
        modifier = Modifier
          .clip(RoundedCornerShape(28.dp))
          .height(72.dp)
          .width(106.dp)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(color = monet.primary)
          ) { viewModel.togglePlayPause() }
      ) {
        PlayPauseButton(
          isPlaying = viewModel.currentState.value == SpPlayerServiceManager.PlaybackState.Playing,
          color = monet.onSecondaryContainer.copy(0.85f) /* if (viewModel.currentBgColor.value != Color.Transparent) viewModel.currentBgColor.value else Color.Black*/,
          modifier = Modifier
            .size(64.dp)
            .align(Alignment.CenterVertically)
        )
      }

      IconButton(
        onClick = { viewModel.skipNext() },
        modifier = Modifier
          .size(56.dp)
          .clip(RoundedCornerShape(28.dp))
          .background(monet.onPrimaryContainer.copy(0.1f)),
        colors = IconButtonDefaults.iconButtonColors(
          contentColor = monet.onSecondaryContainer.copy(0.85f)
        )
      ) {
        Icon(
          imageVector = Icons.Rounded.SkipNext,
          contentDescription = null,
          modifier = Modifier.size(42.dp)
        )
      }
    }

    IconButton(
      onClick = { /*TODO*/ },
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape),
      colors = IconButtonDefaults.iconButtonColors(
        contentColor = monet.onSecondaryContainer.copy(0.85f)
      )
    ) {
      Icon(imageVector = Icons.Rounded.Repeat, contentDescription = null)
    }
  }
}

@Composable
fun ControlsBottomAccessories(
  viewModel: NowPlayingViewModel,
  queueOpened: Boolean,
  setQueueOpened: (Boolean) -> Unit,
) {
  Row(
    modifier = Modifier
      .padding(horizontal = 8.dp)
      .padding(bottom = 16.dp)
      .fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    IconButton(
      onClick = { /*TODO*/ },
      modifier = Modifier
        .size(56.dp),
      colors = IconButtonDefaults.iconButtonColors(
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.85f)
      )
    ) {
      Icon(
        imageVector = Icons.Rounded.VolumeDown,
        contentDescription = null,
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(
            monet.primaryContainer
              .blendWith(monet.primary, 0.3f)
              .copy(0.5f)
          )
          .padding(6.dp)
      )
    }

    IconButton(
      onClick = { setQueueOpened(!queueOpened) },
      modifier = Modifier.size(56.dp),
      colors = IconButtonDefaults.iconButtonColors(
        contentColor = monet.onSecondaryContainer.copy(0.85f)
      )
    ) {
      Icon(
        imageVector = Icons.Rounded.QueueMusic,
        contentDescription = null,
        modifier = Modifier.size(26.dp)
      )
    }
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ArtworkPager(viewModel: NowPlayingViewModel, pagerState: PagerState, animatedCorners: Dp) {
  HorizontalPager(
    count = viewModel.currentQueue.value.size,
    state = pagerState,
    modifier = Modifier
      .fillMaxWidth()
      .height((LocalConfiguration.current.screenWidthDp * 0.9).dp)
  ) { page ->
    val artworkModifier = Modifier
      .size((LocalConfiguration.current.screenWidthDp * 0.9).dp)
      .clip(RoundedCornerShape(animatedCorners))

    if (page == viewModel.currentQueuePosition.value && viewModel.currentTrack.value.artworkCompose != null) {
      Image(
        viewModel.currentTrack.value.artworkCompose!!,
        contentDescription = null,
        modifier = artworkModifier,
        contentScale = ContentScale.Crop
      )
    } else {
      NowPlayingBackgroundItem(
        track = viewModel.currentQueue.value[page],
        modifier = artworkModifier
      )
    }
  }
}