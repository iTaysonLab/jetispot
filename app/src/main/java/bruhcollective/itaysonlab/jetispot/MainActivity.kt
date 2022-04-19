package bruhcollective.itaysonlab.jetispot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import bruhcollective.itaysonlab.jetispot.ui.screens.allScreens
import bruhcollective.itaysonlab.jetispot.ui.screens.dynamic.DynamicSpIdScreen
import bruhcollective.itaysonlab.jetispot.ui.shared.M3Navigation
import bruhcollective.itaysonlab.jetispot.ui.theme.ApplicationTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
  @Inject lateinit var sessionManager: SpSessionManager
  @Inject lateinit var authManager: SpAuthManager
  @Inject lateinit var playerServiceManager: SpPlayerServiceManager

  @OptIn(ExperimentalMaterialApi::class)
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

        val bsState = rememberBottomSheetScaffoldState()

        LaunchedEffect(Unit) {
          if (sessionManager.isSignedIn()) return@LaunchedEffect
          authManager.authStored()
          rootDestination.value = if (sessionManager.isSignedIn()) Screen.Feed.route else Screen.Authorization.route
          currentTab.value = rootDestination.value
        }

        SideEffect {
          sysUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = !isDark)
        }

        Scaffold(
          bottomBar = {
            M3Navigation(navController = navController, bsState = bsState.bottomSheetState, isSelected = { route ->
              currentTab.value == route
            }, onSelect = { route ->
              currentTab.value = route
            })
          }
        ) { innerPadding ->
          BottomSheetScaffold(sheetContent = {
            Box(
              Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.compositeSurfaceElevation(6.dp)))
          }, scaffoldState = bsState, sheetPeekHeight = 80.dp + 72.dp + with(LocalDensity.current) { WindowInsets.navigationBars.getBottom(LocalDensity.current).toDp() }, backgroundColor = MaterialTheme.colorScheme.surface, modifier = Modifier) { innerScaffoldPadding ->
            NavHost(navController, startDestination = rootDestination.value, modifier = Modifier
              .padding(innerScaffoldPadding)) {
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
}