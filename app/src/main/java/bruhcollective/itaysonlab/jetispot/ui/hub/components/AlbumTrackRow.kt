package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub

@OptIn(ExperimentalTextApi::class)
@Composable
fun AlbumTrackRow(
  delegate: HubScreenDelegate,
  item: HubItem
) {
  Column {
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.clickableHub(delegate, item)
    ) {
      Column(Modifier.fillMaxWidth(0.865f).padding(horizontal = 16.dp, vertical = 16.dp)) {
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
            fontWeight = FontWeight.Medium,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            modifier = Modifier
              .padding(top = 4.dp)
              .alpha(0.7f)
          )
        }
      }

      IconButton(
        onClick = { /*TODO*/ },
        modifier = Modifier
          .fillMaxWidth(1f)
          .align(CenterVertically)
      ) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Options for ${item.text!!.title!!} by ${item.text!!.subtitle!!}",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }
    }

    Divider(modifier = Modifier.padding(horizontal = 16.dp).alpha(0.3f))
  }
}