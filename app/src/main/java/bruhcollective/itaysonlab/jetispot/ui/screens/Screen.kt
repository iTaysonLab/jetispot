package bruhcollective.itaysonlab.jetispot.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.ui.screens.auth.AuthScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.ConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.NormalizationConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.QualityConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.HubScreen

sealed class Screen(open val route: String, val screenProvider: @Composable (navController: NavController) -> Unit) {
  // == CORE ==
  object CoreLoadingScreen: Screen("coreLoading", { navController ->
    Box(Modifier.fillMaxSize()) {
      CircularProgressIndicator(modifier = Modifier
        .align(Alignment.Center)
        .size(56.dp))
    }
  }), FullscreenModeScreen

  // == AUTH LAYER ==
  object Authorization: Screen("auth", { navController ->
    AuthScreen(navController)
  }), FullscreenModeScreen

  // == TABS ==
  object Feed: BottomNavigationScreen(
    route = "feed",
    name = R.string.tab_home,
    iconProvider = { Icons.Default.Home },
    screenProvider = { navController ->
      HubScreen(navController, loader = { internal.getHomeView() })
    }
  )

  object Search: BottomNavigationScreen(
    route = "search",
    name = R.string.tab_search,
    iconProvider = { Icons.Default.Search },
    screenProvider = { navController ->
      HubScreen(navController, loader = { internal.getBrowseView() })
    }
  )

  object Library: BottomNavigationScreen(
    route = "library",
    name = R.string.tab_config,
    iconProvider = { Icons.Default.Settings },
    screenProvider = { navController ->
      ConfigScreen(navController)
    }
  )
  //

  object Config: Screen("config", { navController ->
    ConfigScreen(navController)
  })

  object QualityConfig: Screen("config/playbackQuality", { navController ->
    QualityConfigScreen(navController)
  })

  object NormalizationConfig: Screen("config/playbackNormalization", { navController ->
    NormalizationConfigScreen(navController)
  })
}

// Abstracts
abstract class BottomNavigationScreen(@StringRes val name: Int, route: String, val iconProvider: () -> ImageVector, screenProvider: @Composable (NavController) -> Unit): Screen(route, screenProvider)

// Modifiers
interface FullscreenModeScreen

// Extensions
val allScreens = listOf(Screen.CoreLoadingScreen, Screen.Authorization, Screen.Feed, Screen.Search, Screen.Library, Screen.Config, Screen.QualityConfig, Screen.NormalizationConfig).associateBy { it.route }
val bottomNavigationScreens: List<BottomNavigationScreen> = allScreens.values.filterIsInstance<BottomNavigationScreen>()