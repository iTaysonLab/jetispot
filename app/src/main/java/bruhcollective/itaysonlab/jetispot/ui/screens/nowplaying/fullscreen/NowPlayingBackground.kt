package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.ImagePreview
import coil.compose.AsyncImage
import com.spotify.metadata.Metadata

@Composable
fun NowPlayingBackground(
  viewModel: NowPlayingViewModel,
  modifier: Modifier
) {
  val currentColor = viewModel.currentBgColor.value
  val dominantColorAsBg = animateColorAsState(if (currentColor == Color.Transparent) MaterialTheme.colorScheme.surface else currentColor)
  val isSystemInDarkTheme = isSystemInDarkTheme()
  Canvas(modifier) {
    drawRect(
      brush = Brush.radialGradient(
        colors = listOf(dominantColorAsBg.value, if(isSystemInDarkTheme) Color.Black else Color.White),
        center = Offset(
          x = size.width * 0.2f,
          y = size.height * 0.55f
        ),
        radius = size.width * 1.3f
      )
    )
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