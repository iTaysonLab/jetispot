package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate

@Composable
fun Carousel(
  navController: NavController,
  delegate: HubScreenDelegate,
  item: HubItem,
) {
  LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    items(item.children ?: listOf()) { cItem ->
      HubBinder(navController, delegate, cItem)
    }
  }
}