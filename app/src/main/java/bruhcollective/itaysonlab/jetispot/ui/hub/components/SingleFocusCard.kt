package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleFocusCard (
  navController: NavController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  Card(containerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(3.dp), modifier = Modifier
    .height(120.dp)
    .fillMaxWidth()
    .clickableHub(navController, delegate, item)) {
    Row {
      AsyncImage(model = item.images?.main?.uri, contentScale = ContentScale.Crop, contentDescription = null, modifier = Modifier
        .fillMaxHeight()
        .width(120.dp))
      Box(Modifier.fillMaxSize().padding(16.dp)) {
        Column(Modifier.align(Alignment.TopStart)) {
          MediumText(text = item.text!!.title!!)
          Subtext(text = item.text.subtitle!!)
        }
      }
    }
  }
}