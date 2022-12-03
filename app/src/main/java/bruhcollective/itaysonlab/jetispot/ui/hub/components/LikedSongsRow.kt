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
import bruhcollective.itaysonlab.jetispot.SpApp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import kotlinx.coroutines.launch
import bruhcollective.itaysonlab.jetispot.R
import xyz.gianlu.librespot.metadata.ArtistId

@Composable
fun LikedSongsRow(
  item: HubItem
) {
  val delegate = LocalHubScreenDelegate.current
  val likedSongsInfo = remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    launch {
      val count = delegate.getLikedSongsCount(ArtistId.fromBase62((item.events!!.click as HubEvent.NavigateToUri).data.uri.split(":").last()).hexId())
      likedSongsInfo.value = if (count == 0) "" else "$count " + SpApp.context.getString(R.string.songs_by) + " ${item.metadata!!.artist!!.name}"
    }
  }

  if (likedSongsInfo.value.isNotEmpty()) {
    Row(
      Modifier
        .clickableHub(item)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

      Box(Modifier.size(48.dp)) {
        PreviewableAsyncImage(
          imageUrl = item.images?.main?.uri,
          placeholderType = item.images?.main?.placeholder,
          modifier = Modifier
            .clip(CircleShape)
            .align(Alignment.Center)
            .size(48.dp)
        )
        Box(
          Modifier.offset(4.dp, 4.dp).clip(CircleShape).size(28.dp)
            .background(MaterialTheme.colorScheme.surface).align(Alignment.BottomEnd)
        ) {
          Box(
            Modifier.clip(CircleShape).size(22.dp).background(MaterialTheme.colorScheme.primary)
              .align(Alignment.Center)
          ) {
            Icon(
              imageVector = Icons.Rounded.Favorite,
              tint = MaterialTheme.colorScheme.onPrimary,
              contentDescription = null,
              modifier = Modifier.padding(4.dp)
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