package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.core.objs.hub.NavigateUri
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.hub.HubEventHandler
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import coil.compose.AsyncImage
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

    PreviewableAsyncImage(item.images?.main?.uri, item.images?.main?.placeholder, modifier = Modifier
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
            HubEvent.NavigateToUri(NavigateUri(item.metadata.album!!.artists[0].uri!!))
          )
        }
        .padding(horizontal = 16.dp)
        .padding(vertical = 12.dp)) {
        AsyncImage(model = item.metadata.album!!.artists.first().images!![0].uri, contentScale = ContentScale.Crop, contentDescription = null, modifier = Modifier
          .clip(CircleShape)
          .size(32.dp))
        MediumText(text = item.metadata.album.artists.first().name!!, fontSize = 13.sp, modifier = Modifier
          .align(Alignment.CenterVertically)
          .padding(start = 12.dp))
      }
    } else {
      Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        item.metadata.album!!.artists.forEachIndexed { idx, artist ->
          MediumText(text = artist.name!!, fontSize = 13.sp, modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
            HubEventHandler.handle(
              navController,
              delegate,
              HubEvent.NavigateToUri(NavigateUri(artist.uri!!))
            )
          })

          if (idx != item.metadata.album.artists.lastIndex) {
            MediumText(text = " • ", fontSize = 13.sp)
          }
        }
      }
    }

    Subtext(text = "${item.metadata.album!!.type} • ${item.metadata.album.year}", modifier = Modifier.padding(horizontal = 16.dp))

    Row(Modifier.padding(horizontal = 16.dp).padding(bottom = 4.dp)) {
      IconButton(onClick = { /*TODO*/ }, Modifier.offset(y = 2.dp).align(Alignment.CenterVertically).size(28.dp)) {
        Icon(if (delegate.getMainObjectAddedState().value) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null)
      }

      Spacer(Modifier.width(16.dp))

      IconButton(onClick = { /*TODO*/ }, Modifier.offset(y = 2.dp).align(Alignment.CenterVertically).size(28.dp)) {
        Icon(Icons.Default.MoreVert, null)
      }

      Spacer(Modifier.weight(1f))

      Box(Modifier.size(48.dp)) {
        Box(
          Modifier.clip(CircleShape).size(48.dp).background(MaterialTheme.colorScheme.primary).clickableHub(navController, delegate, item.children!![0])
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = null,
            modifier = Modifier.size(32.dp).align(Alignment.Center)
          )
        }

        if (!((item.children[0].events?.click as? HubEvent.PlayFromContext)?.data?.player?.options?.player_options_override?.shuffling_context == false)) {
          Box(
            Modifier.align(Alignment.BottomEnd).offset(4.dp, 4.dp).clip(CircleShape).size(22.dp)
              .background(MaterialTheme.colorScheme.compositeSurfaceElevation(4.dp))
          ) {
            Icon(
              imageVector = Icons.Default.Shuffle,
              tint = MaterialTheme.colorScheme.primary,
              contentDescription = null,
              modifier = Modifier.padding(4.dp).align(Alignment.Center)
            )
          }
        }
      }
    }
  }
}