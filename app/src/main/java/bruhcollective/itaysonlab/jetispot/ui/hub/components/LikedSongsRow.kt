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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MarqueeText
import kotlinx.coroutines.launch
import xyz.gianlu.librespot.metadata.ArtistId

@OptIn(ExperimentalTextApi::class)
@Composable
fun LikedSongsRow(
  item: HubItem
) {
  val delegate = LocalHubScreenDelegate.current
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
        .clickableHub(item)
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
        MarqueeText(
          item.text!!.title!!,
          fontSize = 16.sp,
          style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )
        MarqueeText(
          likedSongsInfo.value,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
          style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
          modifier = Modifier.padding(top = 4.dp).alpha(0.7f)
        )
      }
    }
  }
}