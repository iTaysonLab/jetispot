package bruhcollective.itaysonlab.jetispot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.ui.AppNavigation
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.navigation.NavigationController
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingScreen
import bruhcollective.itaysonlab.jetispot.ui.theme.ApplicationTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        provider?.invoke()?.handleDeepLink(intent)
    }

    override fun onDestroy() {
        provider = null
        super.onDestroy()
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class,
        ExperimentalAnimationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ApplicationTheme {
                val backPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
                // remembers
                val scope = rememberCoroutineScope()

                //q: can a remember bottom sheet scaffold state be used for multiple bottom sheets?
                //a: yes, but you need to use the same scaffold state for all of them

                //In conclusion, you can have multiple bottom sheets, but you can only have one bottom sheet scaffold state
                val bsState = rememberBottomSheetScaffoldState()
                val bottomSheetNavigator = rememberBottomSheetNavigator()
                val navController = rememberAnimatedNavController(bottomSheetNavigator)
                val lambdaNavController = NavigationController { navController }

                //
                val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val shouldHideNavigationBar = navBackStackEntry?.destination?.route == Screen.CoreLoading.route || navBackStackEntry?.destination?.route == Screen.Authorization.route
                val bsVisible = playerServiceManager.playbackState.value != SpPlayerServiceManager.PlaybackState.Idle
                val dockbarHeight = navigationBarHeight + 80.dp + (if (bsVisible) 72.dp else 0.dp)
                val bsPeek by animateDpAsState(if (bsVisible) dockbarHeight else 0.dp)
                val currentRootRoute = remember(navBackStackEntry) {
                    navController.backQueue.getOrNull(1)?.destination?.route
                }
                //


                var bsQueueOpened by remember { mutableStateOf(false) }
                var bsLyricsOpened by remember { mutableStateOf(false) }

                // lambdas
                val bsOffset = {
                    val bsProgress = bsState.bottomSheetState.progress

                    when {
                        shouldHideNavigationBar -> 1f
                        bsProgress.from == BottomSheetValue.Collapsed && bsProgress.to == BottomSheetValue.Collapsed -> 0f
                        bsProgress.from == BottomSheetValue.Expanded && bsProgress.to == BottomSheetValue.Expanded -> 1f
                        bsProgress.to == BottomSheetValue.Expanded -> bsProgress.fraction
                        bsProgress.to == BottomSheetValue.Collapsed -> 1f - bsProgress.fraction
                        else -> bsProgress.fraction
                    }.coerceIn(0f..1f)
                }

                DisposableEffect(
                    backPressedDispatcherOwner,
                    scope,
                    bsState.bottomSheetState.isExpanded,
                    bsQueueOpened,
                    bsLyricsOpened
                ) {
                    val callback = backPressedDispatcherOwner?.onBackPressedDispatcher?.addCallback(
                        owner = backPressedDispatcherOwner,
                        enabled = bsLyricsOpened || bsQueueOpened || bsState.bottomSheetState.isExpanded,
                    ) {
                        if (bsLyricsOpened) {
                            bsLyricsOpened = false
                        } else if (bsQueueOpened) {
                            bsQueueOpened = false
                        } else {
                            scope.launch {
                                bsState.bottomSheetState.collapse()
                            }
                        }
                    }

                    onDispose {
                        callback?.remove()
                    }
                }

                DisposableEffect(navController) {
                    provider = { navController }

                    onDispose {
                        provider = null
                    }
                }

                CompositionLocalProvider(LocalNavigationController provides lambdaNavController) {
                    ModalBottomSheetLayout(
                        bottomSheetNavigator = bottomSheetNavigator,
                        sheetShape = MaterialTheme.shapes.extraLarge.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
                        scrimColor = MaterialTheme.colorScheme.scrim.copy(0.5f),
                        sheetBackgroundColor = MaterialTheme.colorScheme.surface
                    ) {
                        Scaffold(
                            bottomBar = {
                                NavigationBar(
                                    modifier = Modifier
                                        .offset {
                                            IntOffset(0,
                                                (dockbarHeight.toPx() * bsOffset()).toInt()
                                            )
                                        }
                                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                                        .navigationBarsPadding(),
                                ) {
                                    Screen.showInBottomNavigation.forEach { (screen, icon) ->
                                        NavigationBarItem(
                                            icon = {
                                                Icon(
                                                    icon,
                                                    contentDescription = stringResource(screen.title)
                                                )
                                            },
                                            label = { Text(stringResource(screen.title)) },
                                            selected = currentRootRoute == screen.route,
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
                            BottomSheetScaffold(
                                sheetContent = {
                                        NowPlayingScreen(
                                            bottomSheetState = bsState.bottomSheetState,
                                            bsOffset = bsOffset,
                                            queueOpened = bsQueueOpened,
                                            setQueueOpened = { bsQueueOpened = it },
                                            lyricsOpened = bsLyricsOpened,
                                            setLyricsOpened = { bsLyricsOpened = it }
                                        )
                                },
                                scaffoldState = bsState,
                                sheetPeekHeight = bsPeek,
                                backgroundColor = MaterialTheme.colorScheme.surface,
                                sheetGesturesEnabled = !bsQueueOpened && !bsLyricsOpened,
                                modifier = Modifier.background(Color.Transparent)
                            ) { innerScaffoldPadding ->
                                AppNavigation(
                                    navController = navController,
                                    sessionManager = sessionManager,
                                    authManager = authManager,
                                    modifier = Modifier
                                        .padding(innerScaffoldPadding)
                                        .padding(bottom = if (bsVisible) 0.dp else 80.dp + navigationBarHeight)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
