package bruhcollective.itaysonlab.jetispot

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.screens.FullscreenModeScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import bruhcollective.itaysonlab.jetispot.ui.screens.allScreens
import bruhcollective.itaysonlab.jetispot.ui.screens.bottomNavigationScreens
import bruhcollective.itaysonlab.jetispot.ui.screens.dynamic.DynamicSpIdScreen
import bruhcollective.itaysonlab.jetispot.ui.theme.ApplicationTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject lateinit var sessionManager: SpSessionManager
  @Inject lateinit var authManager: SpAuthManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      ApplicationTheme {
        val rootDestination = remember { mutableStateOf(Screen.CoreLoadingScreen.route) }

        val navController = rememberNavController()
        val sysUiController = rememberSystemUiController()
        val isDark = isSystemInDarkTheme()
        val currentTab = remember { mutableStateOf(Screen.Feed.route) }

        LaunchedEffect(Unit) {
          Log.d("SPM", " = LaunchedEffect =")
          if (sessionManager.isSignedIn()) return@LaunchedEffect
          authManager.authStored()
          rootDestination.value = if (sessionManager.isSignedIn()) Screen.Feed.route else Screen.Authorization.route
          currentTab.value = rootDestination.value
        }

        SideEffect {
          Log.d("SPM", " = SideEffect =")
          sysUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = !isDark)
        }

        Scaffold(
          bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val currentDestinationIsFullscreen = allScreens[currentDestination?.route] is FullscreenModeScreen
            if (!currentDestinationIsFullscreen) NavigationBar(modifier = Modifier
              .background(MaterialTheme.colorScheme.compositeSurfaceElevation(3.dp))
              .navigationBarsPadding()) {
              bottomNavigationScreens.forEach { screen ->
                NavigationBarItem(
                  icon = { Icon(screen.iconProvider(), contentDescription = stringResource(screen.name)) },
                  label = { Text(stringResource(screen.name)) },
                  selected = currentTab.value == screen.route,
                  onClick = {
                    currentTab.value = screen.route
                    navController.navigate(screen.route) {
                      popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                      }

                      launchSingleTop = true
                      restoreState = true
                    }
                  }
                )
              }
            }
          }
        ) { innerPadding ->
          NavHost(navController, startDestination = rootDestination.value, Modifier.padding(innerPadding)) {
            allScreens.values.forEach { screen ->
              composable(screen.route) {
                screen.screenProvider(navController)
              }
            }

            composable("spotify:{type}:{id}") {
              DynamicSpIdScreen(navController, it.arguments?.getString("type"), it.arguments?.getString("id"))
            }
          }
        }
      }
    }
  }
}