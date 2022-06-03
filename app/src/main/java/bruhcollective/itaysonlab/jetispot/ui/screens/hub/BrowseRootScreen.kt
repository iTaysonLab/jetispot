package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.runtime.Composable
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController

@Composable
fun BrowseRootScreen(
  navController: LambdaNavigationController
) {
  HubScreen(
    navController,
    loader = { getBrowseView() }
  )
}