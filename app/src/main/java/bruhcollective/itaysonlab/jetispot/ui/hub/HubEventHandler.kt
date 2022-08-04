package bruhcollective.itaysonlab.jetispot.ui.hub

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.navigation.NavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.navClickable

object HubEventHandler {
  fun handle (navController: NavigationController, delegate: HubScreenDelegate, event: HubEvent) {
    when (event) {
      is HubEvent.NavigateToUri -> {
        if (event.data.uri.startsWith("http")) {
          navController.openInBrowser(event.data.uri)
        } else {
          navController.navigate(event.data.uri)
        }
      }
      is HubEvent.PlayFromContext -> delegate.play(event.data)
      HubEvent.Unknown -> {}
    }
  }
}

@Stable
fun Modifier.clickableHub(delegate: HubScreenDelegate, item: HubItem) = navClickable(enabled = item.events?.click != null) { navController ->
  HubEventHandler.handle(navController, delegate, item.events!!.click!!)
}