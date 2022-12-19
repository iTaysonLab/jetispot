package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.runtime.Composable
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder

//Maybe Feed top shortcuts?

@Composable
fun ShortcutsContainer (
  children: List<HubItem>
) {
  children.forEach { item ->
    HubBinder(item)
  }
}