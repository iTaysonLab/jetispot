package bruhcollective.itaysonlab.jetispot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.ui.AppNavigation
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingScreen
import bruhcollective.itaysonlab.jetispot.ui.theme.ApplicationTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
  @Inject
  lateinit var sessionManager: SpSessionManager

  @Inject
  lateinit var authManager: SpAuthManager

  @Inject
  lateinit var playerServiceManager: SpPlayerServiceManager

  private var provider: (() -> NavController)? = null
  private var providerBackPress: () -> Boolean = { false }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    provider?.invoke()?.handleDeepLink(intent)
  }

  override fun onDestroy() {
    provider = null
    super.onDestroy()
  }

  override fun onBackPressed() {
    if (providerBackPress()) return
    super.onBackPressed()
  }

  @OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      ApplicationTheme {
        // remembers
        val scope = rememberCoroutineScope()
        val bsState = rememberBottomSheetScaffoldState()

        val bottomSheetNavigator = rememberBottomSheetNavigator()
        val navController = rememberNavController(bottomSheetNavigator)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val lambdaNavController = LambdaNavigationController { navController }

        val isDark = isSystemInDarkTheme()
        val navBarHeight = WindowInsets.navigationBars.getBottom(LocalDensity.current)
        val navBarHeightDp = with(LocalDensity.current) { navBarHeight.toDp() }

        val bsVisible =
          playerServiceManager.playbackState.value != SpPlayerServiceManager.PlaybackState.Idle
        val bsPeek by animateDpAsState(
          if (bsVisible) {
            with(LocalDensity.current) { (80.dp + 72.dp + navBarHeight.toDp()) }
          } else 0.dp
        )

        // lambdas
        val bsOffset = {
          val bsProgress = bsState.bottomSheetState.progress

          when {
            bsProgress.from == BottomSheetValue.Collapsed && bsProgress.to == BottomSheetValue.Collapsed -> 0f
            bsProgress.from == BottomSheetValue.Expanded && bsProgress.to == BottomSheetValue.Expanded -> 1f
            bsProgress.to == BottomSheetValue.Expanded -> bsProgress.fraction
            bsProgress.to == BottomSheetValue.Collapsed -> 1f - bsProgress.fraction
            else -> bsProgress.fraction
          }.coerceIn(0f..1f)
        }

        LaunchedEffect(Unit) {
          provider = { navController }

          // TODO: figure out how to globally intercept with BackHandler, because NavHost takes it over
          providerBackPress = {
            if (bsState.bottomSheetState.isExpanded) {
              scope.launch { bsState.bottomSheetState.collapse() }
              true
            } else false
          }
        }

        Scaffold(
          bottomBar = {
            val currentDestination = navBackStackEntry?.destination
            if (Screen.hideNavigationBar.any { it == currentDestination?.route }) return@Scaffold
            bruhcollective.itaysonlab.jetispot.ui.shared.evo.NavigationBar(modifier = Modifier
              .offset {
                IntOffset(0, ((80.dp.toPx() + navBarHeight) * bsOffset()).toInt())
              }
              .background(MaterialTheme.colorScheme.compositeSurfaceElevation(3.dp)),
              contentPadding = PaddingValues(bottom = navBarHeightDp)
            ) {
              Screen.showInBottomNavigation.forEach { (screen, icon) ->
                NavigationBarItem(
                  icon = { Icon(icon, contentDescription = stringResource(screen.title)) },
                  label = { Text(stringResource(screen.title)) },
                  selected = lambdaNavController.controller().backQueue.any { it.destination.route?.startsWith(screen.route) == true },
                  onClick = {
                    navController.navigate(screen.route) {
                      popUpTo(Screen.NavGraph.route) {
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
          ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {
            BottomSheetScaffold(
              sheetContent = {
                NowPlayingScreen(
                  navController = lambdaNavController,
                  bottomSheetState = bsState.bottomSheetState,
                  bsOffset = bsOffset
                )
              },
              scaffoldState = bsState,
              sheetPeekHeight = bsPeek,
              backgroundColor = MaterialTheme.colorScheme.surface
            ) { innerScaffoldPadding ->
              AppNavigation(navController = navController, provideLambdaController = lambdaNavController, sessionManager = sessionManager, authManager = authManager, modifier = Modifier
                .padding(innerScaffoldPadding)
                .padding(bottom = if (bsVisible) 0.dp else 80.dp + navBarHeightDp))
            }
          }
        }
      }
    }
  }
}