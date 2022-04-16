package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate

@Composable
fun ShortcutsContainer (
  navController: NavController,
  delegate: HubScreenDelegate,
  children: List<HubItem>
) {
  children.forEach { item ->
    HubBinder(navController, delegate, item)
  }
}