package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import coil.compose.AsyncImage

@Composable
fun MediumCard(
  navController: NavController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  val size = 160.dp
  
  Column(Modifier.width(size).clickableHub(navController, delegate, item)) {
    var drawnTitle = false

    AsyncImage(model = item.images?.main?.uri, contentDescription = null, modifier = Modifier.size(size).clip(
      RoundedCornerShape(if (item.images?.main?.isRounded == true) 12.dp else 0.dp)
    ), contentScale = ContentScale.Crop)

    if (!item.text?.title.isNullOrEmpty()) {
      drawnTitle = true
      MediumText(item.text!!.title!!, modifier = Modifier.padding(top = 8.dp))
    }

    if (!item.text?.subtitle.isNullOrEmpty()) {
      Subtext(item.text!!.subtitle!!, modifier = Modifier.padding(top = if (drawnTitle) 4.dp else 8.dp))
    }
  }
}