package bruhcollective.itaysonlab.jetispot.ui.hub

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController

object HubEventHandler {
  fun handle (navController: LambdaNavigationController, delegate: HubScreenDelegate, event: HubEvent) {
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
fun Modifier.clickableHub(navController: LambdaNavigationController, delegate: HubScreenDelegate, item: HubItem) = this.clickable(enabled = item.events?.click != null) {
  HubEventHandler.handle(navController, delegate, item.events!!.click!!)
}