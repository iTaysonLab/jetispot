package bruhcollective.itaysonlab.jetispot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.util.Log
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import bruhcollective.itaysonlab.jetispot.ui.screens.allScreens
import bruhcollective.itaysonlab.jetispot.ui.screens.dynamic.DynamicSpIdScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingScreen
import bruhcollective.itaysonlab.jetispot.ui.shared.M3Navigation
import bruhcollective.itaysonlab.jetispot.ui.theme.ApplicationTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
  @Inject lateinit var sessionManager: SpSessionManager
  @Inject lateinit var authManager: SpAuthManager
  @Inject lateinit var playerServiceManager: SpPlayerServiceManager

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

  @OptIn(ExperimentalMaterialApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      ApplicationTheme {
        val rootDestination = remember { mutableStateOf(Screen.CoreLoadingScreen.route) }

        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        val sysUiController = rememberSystemUiController()
        val bsState = rememberBottomSheetScaffoldState()
        val currentTab = remember { mutableStateOf(Screen.Feed.route) }

        val isDark = isSystemInDarkTheme()

        val navBarHeight = with(LocalDensity.current) { WindowInsets.navigationBars.getBottom(LocalDensity.current).toDp() }

        val bsVisible = playerServiceManager.playbackState.value != SpPlayerServiceManager.PlaybackState.Idle
        val bsPeek by animateDpAsState(if (bsVisible) 80.dp + 72.dp + navBarHeight else 0.dp)

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

          if (sessionManager.isSignedIn()) {
            if (rootDestination.value == Screen.CoreLoadingScreen.route) {
              rootDestination.value = Screen.Feed.route
            }
            return@LaunchedEffect
          }

          authManager.authStored()
          rootDestination.value = if (sessionManager.isSignedIn()) Screen.Feed.route else Screen.Authorization.route
          currentTab.value = rootDestination.value
        }

        SideEffect {
          sysUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = !isDark)
        }

        Scaffold(
          bottomBar = {
            M3Navigation(navController = navController, bsOffset = bsOffset, isSelected = { route ->
              currentTab.value == route
            }, onSelect = { route ->
              currentTab.value = route
            })
          }
        ) { innerPadding ->
          BottomSheetScaffold(sheetContent = {
            NowPlayingScreen(navController = navController, bottomSheetState = bsState.bottomSheetState, bsOffset = bsOffset)
          }, scaffoldState = bsState, sheetPeekHeight = bsPeek, backgroundColor = MaterialTheme.colorScheme.surface, modifier = Modifier) { innerScaffoldPadding ->
            NavHost(navController, startDestination = rootDestination.value, modifier = Modifier
              .padding(innerScaffoldPadding)
              .padding(bottom = if (bsVisible) 0.dp else 80.dp + navBarHeight)) {
              allScreens.values.forEach { screen ->
                composable(screen.route) {
                  screen.screenProvider(navController)
                }
              }

              composable("spotify:{uri}", deepLinks = listOf(navDeepLink {
                uriPattern = "https://open.spotify.com/{type}/{typeId}"
                action = Intent.ACTION_VIEW
              })) {
                val fullUrl = it.arguments?.getString("uri")
                val dpLinkType = it.arguments?.getString("type")
                val dpLinkTypeId = it.arguments?.getString("typeId")
                val uri = fullUrl ?: "$dpLinkType:$dpLinkTypeId"
                DynamicSpIdScreen(navController, uri, "spotify:$uri")
              }

              dialog("dialogs/logout") {
                AlertDialog(onDismissRequest = { navController.popBackStack() }, icon = {
                  Icon(Icons.Default.Warning, null)
                }, title = {
                  Text(stringResource(id = R.string.logout_title))
                }, text = {
                  Text(stringResource(id = R.string.logout_message))
                }, confirmButton = {
                  Text(stringResource(id = R.string.logout_confirm),
                    Modifier
                      .clickable {
                        navController.popBackStack()
                        authManager.reset()
                        android.os.Process.killProcess(android.os.Process.myPid()) // TODO: maybe dynamic restart the session instances?
                      }
                      .padding(16.dp))
                }, dismissButton = {
                  Text(stringResource(id = R.string.logout_cancel),
                    Modifier
                      .clickable { navController.popBackStack() }
                      .padding(16.dp))
                })
              }
            }
          }
        }
      }
    }
  }
}