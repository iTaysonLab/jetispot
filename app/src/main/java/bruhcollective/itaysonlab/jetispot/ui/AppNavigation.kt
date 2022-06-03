package bruhcollective.itaysonlab.jetispot.ui

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navDeepLink
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.ui.screens.Dialog
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import bruhcollective.itaysonlab.jetispot.ui.screens.auth.AuthScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.ConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.NormalizationConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.QualityConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.dac.DacRendererScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.dynamic.DynamicSpIdScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.BrowseRootScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2.YourLibraryContainerScreen

@Composable
fun AppNavigation(
  navController: NavHostController,
  provideLambdaController: LambdaNavigationController,
  sessionManager: SpSessionManager,
  authManager: SpAuthManager,
  modifier: Modifier
) {
  LaunchedEffect(Unit) {
    if (sessionManager.isSignedIn()) return@LaunchedEffect
    authManager.authStored()
    navController.navigate(if (sessionManager.isSignedIn()) Screen.Feed.route else Screen.Authorization.route) {
      popUpTo(Screen.NavGraph.route)
    }
  }

  NavHost(
    navController = navController,
    startDestination = Screen.CoreLoading.route,
    route = Screen.NavGraph.route,
    modifier = modifier
  ) {
    composable(Screen.CoreLoading.route) {
      Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier
          .align(Alignment.Center)
          .size(56.dp))
      }
    }

    composable(Screen.Authorization.route) {
      AuthScreen(provideLambdaController)
    }

    composable(Screen.Feed.route) {
      DacRendererScreen(provideLambdaController, "", true, {
        getDacHome(SpInternalApi.buildDacRequestForHome(it))
      })
    }

    composable(Screen.SpotifyIdRedirect.route, deepLinks = listOf(navDeepLink {
      uriPattern = Screen.deeplinkCapable[Screen.SpotifyIdRedirect]
      action = Intent.ACTION_VIEW
    })) {
      val fullUrl = it.arguments?.getString("uri")
      val dpLinkType = it.arguments?.getString("type")
      val dpLinkTypeId = it.arguments?.getString("typeId")
      val uri = fullUrl ?: "$dpLinkType:$dpLinkTypeId"
      DynamicSpIdScreen(provideLambdaController, uri, "spotify:$uri")
    }

    composable(Screen.DacViewCurrentPlan.route) {
      DacRendererScreen(provideLambdaController, stringResource(id = Screen.DacViewCurrentPlan.title), false, {
        getPlanOverview()
      })
    }

    composable(Screen.DacViewAllPlans.route) {
      DacRendererScreen(provideLambdaController, stringResource(id = Screen.DacViewAllPlans.title), false, {
        getAllPlans()
      })
    }

    composable(Screen.Config.route) { ConfigScreen(provideLambdaController) }
    composable(Screen.QualityConfig.route) { QualityConfigScreen(provideLambdaController) }
    composable(Screen.NormalizationConfig.route) { NormalizationConfigScreen(provideLambdaController) }
    composable(Screen.Search.route) { BrowseRootScreen(provideLambdaController) }
    composable(Screen.Library.route) { YourLibraryContainerScreen(provideLambdaController) }

    dialog(Dialog.AuthDisclaimer.route) {
      AlertDialog(onDismissRequest = { navController.popBackStack() }, icon = {
        Icon(Icons.Default.Warning, null)
      }, title = {
        Text(stringResource(id = R.string.auth_disclaimer))
      }, text = {
        Text(stringResource(id = R.string.auth_disclaimer_text))
      }, confirmButton = {
        Text(
          stringResource(id = R.string.logout_confirm),
          Modifier
            .clickable { navController.popBackStack() }
            .padding(16.dp))
      })
    }

    dialog(Dialog.Logout.route) {
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

@JvmInline
@Immutable
value class LambdaNavigationController(
  val controller: () -> NavHostController
) {
  @Suppress("DeprecatedCallableAddReplaceWith")
  @Deprecated(message = "Migrate to navigate(Screen) or navigate(Dialog) if not using arguments")
  fun navigate(route: String) = controller().navigate(route)

  fun navigate(screen: Screen) = controller().navigate(screen.route)
  fun navigate(dialog: Dialog) = controller().navigate(dialog.route)

  fun navigateAndClearStack(screen: Screen) = controller().navigate(screen.route) { popUpTo(Screen.NavGraph.route) }

  fun popBackStack() = controller().popBackStack()

  fun context() = controller().context
  fun string(@StringRes id: Int) = context().getString(id)
  fun openInBrowser(uri: String) = context().startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri)))
}