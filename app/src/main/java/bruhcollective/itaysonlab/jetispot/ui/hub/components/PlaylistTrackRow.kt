package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage

@OptIn(ExperimentalTextApi::class)
@Composable
fun PlaylistTrackRow(
  item: HubItem
) {
  Column {
    Row(
      Modifier
        .clickableHub(item)
        .fillMaxWidth()
        .padding(start = 16.dp, end = 2.dp, top = 16.dp, bottom = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(modifier = Modifier.fillMaxWidth(0.865f)) {
        PreviewableAsyncImage(
          imageUrl = item.images?.main?.uri,
          placeholderType = "track",
          modifier = Modifier
            .align(Alignment.CenterVertically)
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
        )

        Column(
          Modifier
            .padding(start = 16.dp)
            .align(Alignment.CenterVertically)
        ) {
          var drawnTitle = false

          if (!item.text?.title.isNullOrEmpty()) {
            drawnTitle = true
            Text(
              item.text!!.title!!,
              fontSize = 16.sp,
              maxLines = 1,
              style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
          }

          if (!item.text?.subtitle.isNullOrEmpty()) {
            Text(
              item.text!!.subtitle!!,
              fontSize = 14.sp,
              maxLines = 1,
              style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
              modifier = Modifier
                .padding(top = if (drawnTitle) 4.dp else 8.dp)
                .alpha(0.7f)
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
          contentDescription = "Options for ${item.text?.title} by ${item.text?.subtitle}",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }
    }

    Divider(Modifier.padding(horizontal = 16.dp).alpha(0.3f))
  }
}