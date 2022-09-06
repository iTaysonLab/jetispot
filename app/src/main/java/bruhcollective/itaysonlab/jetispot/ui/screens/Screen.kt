package bruhcollective.itaysonlab.jetispot.ui.screens

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Immutable
import bruhcollective.itaysonlab.jetispot.R

@Immutable
enum class Screen(
  val route: String,
  @StringRes val title: Int = 0,
) {
  // internal
  NavGraph("nav_graph"),
  CoreLoading("coreLoading"),
  Authorization("auth"),
  SpotifyIdRedirect("spotify:{uri}"),
  // bottom
  Feed("feed", title = R.string.tab_home),
  Search("search", title = R.string.tab_search),
  Library("library", title = R.string.tab_library),
  // hubs/dac
  DacViewCurrentPlan("dac/viewCurrentPlan", title = R.string.plan_overview),
  DacViewAllPlans("dac/viewAllPlans", title = R.string.all_plans),
  // config
  Config("config"),
  StorageConfig("config/storage"),
  QualityConfig("config/playbackQuality"),
  NormalizationConfig("config/playbackNormalization");

  companion object {
    val hideNavigationBar = setOf(CoreLoading.route, Authorization.route, Dialog.AuthDisclaimer.route)
    val deeplinkCapable = mapOf(SpotifyIdRedirect to "https://open.spotify.com/{type}/{typeId}")
    val showInBottomNavigation = mapOf(
      Feed to Icons.Rounded.Home,
      Search to Icons.Rounded.Search,
      Library to Icons.Rounded.LibraryMusic
    )
  }
}

@Immutable
enum class Dialog(
  val route: String
) {
  AuthDisclaimer("dialogs/disclaimers"),
  ColorSelect("dialogs/ColorSelect"),
  Logout("dialogs/logout"),
  SetArtworkData("dialogs/SetArtworkData")
}

@Immutable
enum class BottomSheet(
  val route: String
) {
  JumpToArtist("bs/jumpToArtist/{artistIdsAndRoles}") // ID=ROLE|ID=ROLE
}
