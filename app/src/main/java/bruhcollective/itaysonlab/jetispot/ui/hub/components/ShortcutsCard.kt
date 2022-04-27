package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortcutsCard(
  navController: NavController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  Card(containerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(3.dp), modifier = Modifier.height(56.dp).fillMaxWidth().clickableHub(navController, delegate, item)) {
    Row {
      PreviewableAsyncImage(imageUrl = item.images?.main?.uri, placeholderType = item.images?.main?.placeholder, modifier = Modifier.size(56.dp))
      Text(item.text!!.title!!, fontSize = 13.sp, lineHeight = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 8.dp))
    }
  }
}