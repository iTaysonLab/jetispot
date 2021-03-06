package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.runtime.Composable
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate

@Composable
fun ShortcutsContainer (
  navController: LambdaNavigationController,
  delegate: HubScreenDelegate,
  children: List<HubItem>
) {
  children.forEach { item ->
    HubBinder(navController, delegate, item)
  }
}