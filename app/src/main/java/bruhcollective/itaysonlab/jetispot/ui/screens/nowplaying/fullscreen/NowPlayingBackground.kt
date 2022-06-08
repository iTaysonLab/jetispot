package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.ui.ext.blendWith
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.ImagePreview
import bruhcollective.itaysonlab.jetispot.ui.theme.ApplicationTheme
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.spotify.metadata.Metadata
import androidx.compose.material3.MaterialTheme.colorScheme as monet

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NowPlayingBackground(
  state: PagerState,
  viewModel: NowPlayingViewModel,
  modifier: Modifier
) {
  ApplicationTheme {
    val currentColor = viewModel.currentBgColor.value
    val dominantColorAsBg = if (isSystemInDarkTheme())
      monet.surface.blendWith(monet.primary, ratio = 0.05f)
    else
      monet.primary.copy(0.1f)/* animateColorAsState(
    if (currentColor == Color.Transparent) MaterialTheme.colorScheme.surface else currentColor
  ) */

    Box(modifier = modifier.background(dominantColorAsBg)) {
      HorizontalPager(
        count = viewModel.currentQueue.value.size,
        state = state,
        modifier = modifier
      ) { page ->
        val artworkModifier = Modifier
          .align(Alignment.Center)
          .padding(bottom = (LocalConfiguration.current.screenHeightDp * 0.30).dp)
          .size((LocalConfiguration.current.screenWidthDp * 0.9).dp)
          .clip(RoundedCornerShape(32.dp))

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