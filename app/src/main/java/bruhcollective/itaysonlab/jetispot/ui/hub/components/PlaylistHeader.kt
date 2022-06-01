package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.ext.blendWith
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.components.essentials.EntityActionStrip
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun PlaylistHeader(
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
      dominantColor.value =
        delegate.calculateDominantColor(item.images?.main?.uri.toString(), darkTheme)
    }
  }

  Column(
    Modifier
      .fillMaxHeight()
      .fillMaxWidth()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(dominantColorAsBg.value, Color.Transparent)
        )
      )
      .padding(top = 16.dp)
      .statusBarsPadding()
  ) {
    Image(
      painter = rememberAsyncImagePainter(model = item.images?.main?.uri),
      contentDescription = null,
      modifier = Modifier
        .size((LocalConfiguration.current.screenWidthDp * 0.7).dp)
        .align(Alignment.CenterHorizontally)
        .padding(bottom = 8.dp)
    )

    MediumText(
      text = item.text?.title!!, fontSize = 21.sp, modifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 8.dp)
    )

    Text(
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
      fontSize = 12.sp,
      lineHeight = 18.sp,
      text = item.text.subtitle!!, modifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 8.dp)
    )

    EntityActionStrip(navController, delegate, item)
  }
}

@Composable
fun LargePlaylistHeader(
  navController: NavController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  Column {
    Box(
      Modifier
        .fillMaxWidth()
        .height(240.dp)
    ) {
      AsyncImage(
        model = item.images?.main?.uri,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = Modifier
          .fillMaxSize()
      )

      Box(Modifier.background(
        brush = Brush.verticalGradient(
          colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
        )
      ).fillMaxSize())

      MediumText(
        text = item.text?.title!!,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        maxLines = 2,
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(horizontal = 16.dp)
          .padding(bottom = 8.dp)
      )
    }

    Text(
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
      fontSize = 12.sp,
      lineHeight = 18.sp,
      text = item.text?.subtitle!!, modifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 16.dp)
    )

    EntityActionStrip(navController, delegate, item)
  }
}