package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubEventHandler
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText

@Composable
fun OutlineButton(
    navController: NavController,
    delegate: HubScreenDelegate,
    item: HubItem
) {
    OutlinedButton(
        onClick = {
            HubEventHandler.handle(navController, delegate, item.events!!.click!!)
        },
        Modifier.padding(16.dp, 0.dp)
    ) {
        MediumText(text = item.text?.title!!)
    }
}