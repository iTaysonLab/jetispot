package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.runtime.Composable

@Composable
fun BrowseRootScreen() {
  HubScreen(loader = { getBrowseView() })
}