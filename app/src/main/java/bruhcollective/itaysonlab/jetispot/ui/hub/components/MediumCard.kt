package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage

@OptIn(ExperimentalTextApi::class)
@Composable
fun MediumCard(item: HubItem) {
  Surface(
    color = MaterialTheme.colorScheme.background,
    shape = RoundedCornerShape(20.dp)
  ) {
    Column(
      horizontalAlignment = CenterHorizontally,
      modifier = Modifier
        .width(172.dp)
        .clickableHub(item)
        .padding(bottom = 12.dp)
    ) {
      var drawnTitle = false

      // Had to wrap the image in another composable due to weird padding when
      // image couldn't be retrieved
      Surface(Modifier.padding(top = 6.dp)) {
        PreviewableAsyncImage(
          imageUrl = item.images?.main?.uri,
          placeholderType = item.images?.main?.placeholder,
          modifier = Modifier
            .size(160.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
        )
      }

      // Title of the card. TODO: Scrolling text
      Column(
        Modifier.height(64.dp).padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.Center
      ) {
        if (!item.text?.title.isNullOrEmpty()) {
          drawnTitle = true
          Text(
            item.text!!.title!!,
            fontSize = 16.sp,
//            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            textAlign = if (item.text?.subtitle.isNullOrEmpty() && item.text?.description.isNullOrEmpty())
              TextAlign.Center
            else
              TextAlign.Start
          )
        }

        if (!item.text?.subtitle.isNullOrEmpty()) {
          Text(
            item.text!!.subtitle!!,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
              .padding(top = if (drawnTitle) 4.dp else 0.dp)
              .fillMaxWidth(),
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Start
          )
        } else if (!item.text?.description.isNullOrEmpty()) {
          Text(
            item.text!!.description!!,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = if (drawnTitle) 4.dp else 0.dp),
            fontWeight = FontWeight.Medium,
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
          )
        }
      }
    }
  }
}