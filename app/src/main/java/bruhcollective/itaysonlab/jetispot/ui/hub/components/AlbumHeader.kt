package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun AlbumHeader(
  navController: NavController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  Column(modifier = Modifier
    .fillMaxHeight()
    .statusBarsPadding()) {

    AsyncImage(model = item.images?.main?.uri, contentDescription = null,
      Modifier
        .size((LocalConfiguration.current.screenWidthDp * 0.7).dp)
        .align(Alignment.CenterHorizontally)
        .padding(bottom = 8.dp))

    MediumText(text = item.text!!.title!!, fontSize = 21.sp, modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp))

    if (item.metadata!!.album!!.artists.size == 1) {
      // large
      Row(modifier = Modifier
        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
          HubEventHandler.handle(navController, delegate, HubEvent.NavigateToUri(NavigateUri(item.metadata.album!!.artists[0].uri)))
        }
        .padding(horizontal = 16.dp)
        .padding(vertical = 12.dp)) {
        AsyncImage(model = item.metadata.album!!.artists.first().images[0].uri, contentDescription = null, modifier = Modifier
          .clip(CircleShape)
          .size(32.dp))
        MediumText(text = item.metadata.album.artists.first().name, fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp))
      }
    } else {
      MediumText(text = item.metadata.album!!.artists.joinToString(" • ") { it.name }, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    }

    Subtext(text = "${item.metadata.album!!.type} • ${item.metadata.album.year}", modifier = Modifier.padding(horizontal = 16.dp))
  }
}