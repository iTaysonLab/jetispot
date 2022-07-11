package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.ui.shared.ImagePreview
import coil.compose.AsyncImage
import com.spotify.metadata.Metadata

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