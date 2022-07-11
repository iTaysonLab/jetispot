package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun MediumCard(
  navController: LambdaNavigationController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  val size = 160.dp

  Surface(
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(20.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .width(size)
        .clickableHub(navController, delegate, item)
        .padding(bottom = 10.dp)
    ) {
      var drawnTitle = false

      PreviewableAsyncImage(
        imageUrl = item.images?.main?.uri,
        placeholderType = item.images?.main?.placeholder,
        modifier = Modifier
          .padding(top = 8.dp)
          .size(144.dp)
          .clip(RoundedCornerShape(if (item.images?.main?.isRounded == true) 12.dp else 16.dp))
      )

      Column(
        Modifier.height(68.dp).padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Center
      ) {
        if (!item.text?.title.isNullOrEmpty()) {
          drawnTitle = true
          Text(
            item.text!!.title!!,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp, start = 0.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = TextStyle(platformStyle = PlatformTextStyle(false))
          )
        }

        if (!item.text?.subtitle.isNullOrEmpty()) {
          Text(
            item.text!!.subtitle!!,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
              .padding(top = if (drawnTitle) 4.dp else 8.dp)
              .padding(start = 8.dp),
            style = TextStyle(platformStyle = PlatformTextStyle(false))
          )
        } else if (!item.text?.description.isNullOrEmpty()) {
          Text(
            item.text!!.description!!,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = if (drawnTitle) 4.dp else 8.dp),
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            maxLines = 2
          )
        }
      }
    }
  }
}