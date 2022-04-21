package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.core.objs.hub.NavigateUri
import bruhcollective.itaysonlab.jetispot.ui.hub.HubEventHandler
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun AlbumHeader(
  navController: NavController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  val darkTheme = isSystemInDarkTheme()
  val dominantColor = remember { mutableStateOf(Color.Transparent) }
  val dominantColorAsBg = animateColorAsState(dominantColor.value)

  LaunchedEffect(Unit) {
    launch {
      if (dominantColor.value != Color.Transparent) return@launch
      dominantColor.value = delegate.calculateDominantColor(item.images?.main?.uri.toString(), darkTheme)
    }
  }

  Column(modifier = Modifier
    .fillMaxHeight()
    .background(
      brush = Brush.verticalGradient(
        colors = listOf(dominantColorAsBg.value, Color.Transparent)
      )
    )
    .padding(top = 16.dp)
    .statusBarsPadding()) {

    Image(painter = rememberAsyncImagePainter(model = item.images?.main?.uri), contentDescription = null, modifier = Modifier
      .size((LocalConfiguration.current.screenWidthDp * 0.7).dp)
      .align(Alignment.CenterHorizontally)
      .padding(bottom = 8.dp))

    MediumText(text = item.text!!.title!!, fontSize = 21.sp, modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(top = 8.dp))

    if (item.metadata!!.album!!.artists.size == 1) {
      // large
      Row(modifier = Modifier
        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
          HubEventHandler.handle(
            navController,
            delegate,
            HubEvent.NavigateToUri(NavigateUri(item.metadata.album!!.artists[0].uri))
          )
        }
        .padding(horizontal = 16.dp)
        .padding(vertical = 12.dp)) {
        AsyncImage(model = item.metadata.album!!.artists.first().images[0].uri, contentScale = ContentScale.Crop, contentDescription = null, modifier = Modifier
          .clip(CircleShape)
          .size(32.dp))
        MediumText(text = item.metadata.album.artists.first().name, fontSize = 13.sp, modifier = Modifier
          .align(Alignment.CenterVertically)
          .padding(start = 12.dp))
      }
    } else {
      MediumText(text = item.metadata.album!!.artists.joinToString(" • ") { it.name }, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    }

    Subtext(text = "${item.metadata.album!!.type} • ${item.metadata.album.year}", modifier = Modifier.padding(horizontal = 16.dp))
  }
}