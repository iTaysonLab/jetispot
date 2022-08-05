package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortcutsContainer (
  delegate: HubScreenDelegate,
  children: List<HubItem>
) {
  children.forEach { item ->
    HubBinder(delegate, item)
  }
}