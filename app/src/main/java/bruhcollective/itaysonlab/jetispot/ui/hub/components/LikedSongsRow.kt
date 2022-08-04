package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import kotlinx.coroutines.launch
import xyz.gianlu.librespot.metadata.ArtistId

@Composable
fun LikedSongsRow(
  navController: LambdaNavigationController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  val likedSongsInfo = remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    launch {
      val count = delegate.getLikedSongsCount(ArtistId.fromBase62((item.events!!.click as HubEvent.NavigateToUri).data.uri.split(":").last()).hexId())
      likedSongsInfo.value = if (count == 0) "" else "$count songs by ${item.metadata!!.artist!!.name}"
    }
  }

  if (likedSongsInfo.value.isNotEmpty()) {
    Row(
      Modifier
        .clickableHub(navController, delegate, item)
        .padding(start = 16.dp, top = 16.dp)
    ) {

      Box(Modifier.size(48.dp)) {
        Box(Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.tertiaryContainer)) {
          Box(
            Modifier
              .clip(CircleShape)
              .size(48.dp)
          ) {
            Icon(
              imageVector = Icons.Rounded.Favorite,
              tint = MaterialTheme.colorScheme.onTertiaryContainer,
              contentDescription = null,
              modifier = Modifier.align(Alignment.Center)
            )
          }
        }
      }

      Column(
        Modifier
          .align(Alignment.CenterVertically)
          .padding(start = 16.dp)
      ) {
        MediumText(item.text!!.title!!, fontWeight = FontWeight.Normal)
        Subtext(likedSongsInfo.value, modifier = Modifier.padding(top = 4.dp))
      }
    }
  }
}