package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.navigation.NavigationController

fun Modifier.navClickable(
    enabled: Boolean = true,
    enableRipple: Boolean = true,
    onClick: (NavigationController) -> Unit
) = composed {
    val navController = LocalNavigationController.current

    Modifier.clickable(
        enabled = enabled,
        indication = if (enableRipple) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick(navController)
    }
}

fun Modifier.navAndHubClickable(
    enabled: Boolean = true,
    enableRipple: Boolean = true,
    onClick: (NavigationController, HubScreenDelegate) -> Unit
) = composed {
    val navController = LocalNavigationController.current
    val hubScreenDelegate = LocalHubScreenDelegate.current

    Modifier.clickable(
        enabled = enabled,
        indication = if (enableRipple) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick(navController, hubScreenDelegate)
    }
}