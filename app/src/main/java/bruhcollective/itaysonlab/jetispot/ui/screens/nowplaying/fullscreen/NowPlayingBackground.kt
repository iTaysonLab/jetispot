package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.ImagePreview
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.spotify.metadata.Metadata

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NowPlayingBackground(
  state: PagerState,
  viewModel: NowPlayingViewModel,
  modifier: Modifier
) {
  val currentColor = viewModel.currentBgColor.value
  val dominantColorAsBg = animateColorAsState(
    if (currentColor == Color.Transparent) MaterialTheme.colorScheme.surface else currentColor
  )

  Box(modifier = modifier.background(dominantColorAsBg.value)) {
    HorizontalPager(
      count = viewModel.currentQueue.value.size,
      state = state,
      modifier = modifier
    ) { page ->
      val artworkModifier = Modifier
        .align(Alignment.Center)
        .padding(bottom = (LocalConfiguration.current.screenHeightDp * 0.25).dp)
        .size((LocalConfiguration.current.screenWidthDp * 0.9).dp)

      if (page == viewModel.currentQueuePosition.value && viewModel.currentTrack.value.artworkCompose != null) {
        Image(viewModel.currentTrack.value.artworkCompose!!, contentDescription = null, modifier = artworkModifier, contentScale = ContentScale.Crop)
      } else {
        NowPlayingBackgroundItem(
          track = viewModel.currentQueue.value[page],
          modifier = artworkModifier
        )
      }
    }
  }
}

@Composable
fun NowPlayingBackgroundItem(
  track: Metadata.Track,
  modifier: Modifier,
) {
  Box(modifier) {
    ImagePreview("track", Modifier.fillMaxSize())
    AsyncImage(
      model = SpUtils.getImageUrl(track.album.coverGroup.imageList.find { it.size == Metadata.Image.Size.LARGE }?.fileId),
      contentDescription = null,
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop,
    )
  }
}