package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.MarqueeText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage

@Composable
fun ArtistTrackRow(
  item: HubItem
) {
  val navController = LocalNavigationController.current
  Row(
    Modifier
      .clickableHub(item)
      .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(0.865f)
    ) {
      Text(text = (item.custom!!["rowNumber"] as Double).toInt().toString(), modifier = Modifier
        .align(Alignment.CenterVertically)
        .padding(end = 16.dp)
      )

      PreviewableAsyncImage(
        imageUrl = item.images?.main?.uri,
        placeholderType = item.images?.main?.placeholder,
        modifier = Modifier
          .align(Alignment.CenterVertically)
          .size(48.dp)
          .clip(RoundedCornerShape(8.dp))
      )

      Column(
        Modifier
          .align(Alignment.CenterVertically)
          .padding(start = 16.dp)
      ) {
        var drawnTitle = false

        if (!item.text?.title.isNullOrEmpty()) {
          drawnTitle = true
          MarqueeText(
            item.text!!.title!!,
            fontSize = 16.sp
          )
        }

        if (!item.text?.subtitle.isNullOrEmpty()) {
          MarqueeText(
            item.text!!.subtitle!!,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
            modifier = Modifier.padding(top = if (drawnTitle) 4.dp else 8.dp)
          )
        }
      }
    }

    IconButton(
      onClick = { /*TODO*/ },
      modifier = Modifier
        .fillMaxWidth(1f)
        .align(Alignment.CenterVertically)
    ) {
      Icon(
        imageVector = Icons.Default.MoreVert,
        contentDescription = "Options for ${item.text!!.title!!} by ${item.text!!.subtitle!!}",
        tint = MaterialTheme.colorScheme.onBackground
      )
    }
  }
}