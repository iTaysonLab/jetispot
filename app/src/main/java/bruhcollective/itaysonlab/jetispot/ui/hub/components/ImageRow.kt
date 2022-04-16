package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubText
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.SubtextOverline
import coil.compose.AsyncImage

@Composable
fun ImageRow(
  navController: NavController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  Row(Modifier.clickableHub(navController, delegate, item).padding(horizontal = 16.dp, vertical = 12.dp)) {
    AsyncImage(model = item.images?.main?.uri, contentDescription = null, modifier = Modifier
      .size(42.dp)
      .clip(CircleShape), contentScale = ContentScale.Crop)

    Column(Modifier.padding(horizontal = 12.dp).align(Alignment.CenterVertically)) {
      MediumText(item.text!!.title!!, fontWeight = FontWeight.Normal, fontSize = 18.sp)
    }
  }
}