package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BrowseRootScreen(
  navController: NavController
) {
  HubScreen(
    navController,
    loader = { getBrowseView() }
  )
}