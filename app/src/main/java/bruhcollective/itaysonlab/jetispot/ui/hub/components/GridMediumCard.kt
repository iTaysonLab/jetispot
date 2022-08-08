package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage

@OptIn(ExperimentalTextApi::class)
@Composable
fun GridMediumCard(
  item: HubItem
) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
    shape = RoundedCornerShape(24.dp),
    modifier = Modifier
      .height(264.dp)
      .width(164.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.clickableHub(item).padding(14.dp)
    ) {
      var drawnTitle = false
      Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)) {
        PreviewableAsyncImage(
          imageUrl = item.images?.main?.uri,
          placeholderType = item.images?.main?.placeholder,
          modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
        )
      }

      Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
        drawnTitle = true
        androidx.compose.material3.Text(
          text = item.text!!.title!!,
          fontSize = 16.sp,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )

        if (!item.text?.subtitle.isNullOrEmpty()) {
          androidx.compose.material.Text(
            item.text!!.subtitle!!,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
              .padding(top = if (drawnTitle) 4.dp else 0.dp)
          )
        } else if (!item.text?.description.isNullOrEmpty()) {
          androidx.compose.material.Text(
            item.text!!.description!!,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
              .padding(top = if (drawnTitle) 4.dp else 0.dp)
          )
        }
      }
    }
  }
}