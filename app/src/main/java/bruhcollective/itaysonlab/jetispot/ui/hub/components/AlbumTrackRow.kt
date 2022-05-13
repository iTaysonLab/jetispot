package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import coil.compose.AsyncImage

@Composable
fun AlbumTrackRow(
  navController: NavController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  Column(Modifier.clickableHub(navController, delegate, item).fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
    var drawnTitle = false

    if (!item.text?.title.isNullOrEmpty()) {
      drawnTitle = true
      MediumText(item.text!!.title!!, fontWeight = FontWeight.Normal)
    }

    if (!item.text?.subtitle.isNullOrEmpty()) {
      Subtext(item.text!!.subtitle!!, modifier = Modifier.padding(top = if (drawnTitle) 4.dp else 8.dp))
    }
  }
}