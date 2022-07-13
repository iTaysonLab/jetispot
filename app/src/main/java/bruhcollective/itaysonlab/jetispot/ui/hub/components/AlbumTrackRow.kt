package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext

@Composable
fun AlbumTrackRow(
  navController: LambdaNavigationController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  Row(horizontalArrangement = Arrangement.SpaceBetween) {
    Column(
      Modifier
        .clickableHub(navController, delegate, item)
        .fillMaxWidth(0.88f)
        .padding(horizontal = 16.dp, vertical = 12.dp)) {
      var drawnTitle = false

      if (!item.text?.title.isNullOrEmpty()) {
        drawnTitle = true
        MediumText(item.text!!.title!!, fontWeight = FontWeight.Normal)
      }

      if (!item.text?.subtitle.isNullOrEmpty()) {
        Subtext(item.text!!.subtitle!!, modifier = Modifier.padding(top = if (drawnTitle) 4.dp else 8.dp))
      }
    }
    IconButton(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth(1f).align(CenterVertically)) {
      Icon(
        imageVector = Icons.Default.MoreVert,
        contentDescription = "Options for ${item.text!!.title!!} by ${item.text!!.subtitle!!}"
      )
    }
  }
}