package bruhcollective.itaysonlab.jetispot.ui.screens

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import bruhcollective.itaysonlab.jetispot.R

@Immutable
enum class Screen(
  val route: String,
  val icon: ImageVector? = null,
  @StringRes val title: Int = 0,
) {
  // internal
  NavGraph("nav_graph"),
  CoreLoading("coreLoading"),
  Authorization("auth"),
  SpotifyIdRedirect("spotify:{uri}"),
  // bottom
  Feed("feed", title = R.string.tab_home, icon = Icons.Rounded.Home),
  Search("search", title = R.string.tab_search, icon = Icons.Rounded.Search),
  Library("library", title = R.string.tab_library, icon = Icons.Rounded.LibraryMusic),
  // hubs/dac
  DacViewCurrentPlan("dac/viewCurrentPlan", title = R.string.plan_overview),
  DacViewAllPlans("dac/viewAllPlans", title = R.string.all_plans),
  // config
  Config("config"),
  QualityConfig("config/playbackQuality"),
  NormalizationConfig("config/playbackNormalization");

  companion object {
    val hideNavigationBar = setOf(CoreLoading.route, Authorization.route, Dialog.AuthDisclaimer.route)
    val showInBottomNavigation = setOf(Feed, Search, Library)
    val deeplinkCapable = mapOf(SpotifyIdRedirect to "https://open.spotify.com/{type}/{typeId}")
  }
}

@Immutable
enum class Dialog(
  val route: String
) {
  AuthDisclaimer("dialogs/disclaimers"),
  Logout("dialogs/logout")
}