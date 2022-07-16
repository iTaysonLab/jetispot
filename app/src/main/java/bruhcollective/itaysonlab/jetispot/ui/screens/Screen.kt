package bruhcollective.itaysonlab.jetispot.ui.screens

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
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
      Feed to when (NavGraph) {
        Feed -> Icons.Rounded.Home
        else -> Icons.Outlined.Home
      },
      Search to when (NavGraph) {
        Search -> Icons.Rounded.Search
        else -> Icons.Outlined.Search
      },
      Library to when (NavGraph) {
        Library -> Icons.Rounded.LibraryMusic
        else -> Icons.Outlined.LibraryMusic
      }
    )
  }
}

@Immutable
enum class Dialog(
  val route: String
) {
  AuthDisclaimer("dialogs/disclaimers"),
  Logout("dialogs/logout")
}

@Immutable
enum class BottomSheet(
  val route: String
) {
  JumpToArtist("bs/jumpToArtist/{artistIdsAndRoles}") // ID=ROLE|ID=ROLE
}
