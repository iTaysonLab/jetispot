package bruhcollective.itaysonlab.jetispot

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bruhcollective.itaysonlab.jetispot.SpApp.Companion.applicationScope
import bruhcollective.itaysonlab.jetispot.SpApp.Companion.context
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.util.Log
import bruhcollective.itaysonlab.jetispot.ui.AppNavigation
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.navigation.NavigationController
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingScreen
import bruhcollective.itaysonlab.jetispot.ui.theme.ApplicationTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    @OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking{
            updateLanguage(context, LocaleHelper.getLanguage(context))
        }

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
                val navController = rememberNavController(bottomSheetNavigator)
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val lambdaNavController = NavigationController { navController }

                val navBarHeightDp =
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

                val bsVisible =
                    playerServiceManager.playbackState.value != SpPlayerServiceManager.PlaybackState.Idle
                val bsPeek by animateDpAsState(
                    if (bsVisible) 80.dp + 72.dp + navBarHeightDp else 0.dp
                )

                var bsQueueOpened by remember { mutableStateOf(false) }
                var bsLyricsOpened by remember { mutableStateOf(false) }

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
                    ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {
                        Scaffold(
                            bottomBar = {
                                val currentDestination = navBackStackEntry?.destination
                                if (Screen.hideNavigationBar.any { it == currentDestination?.route }) return@Scaffold
                                NavigationBar(
                                    modifier = Modifier
                                        .offset {
                                            IntOffset(
                                                0,
                                                ((80.dp + navBarHeightDp).toPx() * bsOffset()).toInt()
                                            )
                                        }
                                        .background(
                                            MaterialTheme.colorScheme.compositeSurfaceElevation(
                                                3.dp
                                            )
                                        )
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
                                            selected = lambdaNavController.controller().backQueue.any {
                                                it.destination.route?.startsWith(
                                                    screen.route
                                                ) == true
                                            },
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
                                sheetGesturesEnabled = !bsQueueOpened && !bsLyricsOpened
                            ) { innerScaffoldPadding ->
                                AppNavigation(
                                    navController = navController,
                                    sessionManager = sessionManager,
                                    authManager = authManager,
                                    modifier = Modifier
                                        .padding(innerScaffoldPadding)
                                        .padding(bottom = if (bsVisible) 0.dp else 80.dp + navBarHeightDp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    companion object{

        fun updateLanguage(context: Context = SpApp.context, language: String) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = Configuration()
            if (language.isEmpty()) {
                val emptyLocaleList = LocaleListCompat.getEmptyLocaleList()
                config.setLocale(emptyLocaleList[0])
            } else {
                config.setLocale(locale)
            }

            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}
