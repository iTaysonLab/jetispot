package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.core.objs.hub.NavigateUri
import bruhcollective.itaysonlab.jetispot.ui.hub.HubEventHandler
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.components.essentials.EntityActionStrip
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import bruhcollective.itaysonlab.jetispot.ui.shared.navClickable
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun AlbumHeader(
    item: HubItem
) {
    val darkTheme = isSystemInDarkTheme()
    val dominantColor = remember { mutableStateOf(Color.Transparent) }
    val dominantColorAsBg = animateColorAsState(dominantColor.value)
    val delegate = LocalHubScreenDelegate.current

    LaunchedEffect(Unit) {
        launch {
            if (dominantColor.value != Color.Transparent) return@launch
            dominantColor.value =
                delegate.calculateDominantColor(item.images?.main?.uri.toString(), darkTheme)
        }
    }

    Column(
        modifier = Modifier
          .fillMaxHeight()
          .background(
            brush = Brush.verticalGradient(
              colors = listOf(dominantColorAsBg.value, Color.Transparent)
            )
          )
          .padding(top = 16.dp)
          .statusBarsPadding()
    ) {

        PreviewableAsyncImage(
            item.images?.main?.uri, item.images?.main?.placeholder, modifier = Modifier
            .size((LocalConfiguration.current.screenWidthDp * 0.7).dp)
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 8.dp)
        )

        MediumText(
            text = item.text!!.title!!, fontSize = 21.sp, modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
        )

        if (item.metadata!!.album!!.artists.size == 1) {
            // large
            Row(modifier = Modifier
              .navClickable(enableRipple = false) { navController ->
                HubEventHandler.handle(
                  navController,
                  delegate,
                  HubEvent.NavigateToUri(NavigateUri(item.metadata.album!!.artists[0].uri!!))
                )
              }
              .padding(horizontal = 16.dp)
              .padding(vertical = 12.dp)) {
                AsyncImage(
                    model = item.metadata.album!!.artists.first().images!![0].uri,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                      .clip(CircleShape)
                      .size(32.dp)
                )
                MediumText(
                    text = item.metadata.album.artists.first().name!!,
                    fontSize = 13.sp,
                    modifier = Modifier
                      .align(Alignment.CenterVertically)
                      .padding(start = 12.dp)
                )
            }
        } else {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                item.metadata.album!!.artists.forEachIndexed { idx, artist ->
                    MediumText(
                        text = artist.name!!,
                        fontSize = 13.sp,
                        modifier = Modifier.navClickable(enableRipple = false) { navController ->
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

        Subtext(
            text = "${item.metadata.album!!.type} • ${item.metadata.album.year}",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        EntityActionStrip(delegate, item)
    }
}