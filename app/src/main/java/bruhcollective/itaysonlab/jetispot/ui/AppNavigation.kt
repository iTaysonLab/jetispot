package bruhcollective.itaysonlab.jetispot.ui

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import androidx.navigation.navDeepLink
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.ui.bottomsheets.jump_to_artist.JumpToArtistBottomSheet
import bruhcollective.itaysonlab.jetispot.ui.screens.BottomSheet
import bruhcollective.itaysonlab.jetispot.ui.screens.Dialog
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import bruhcollective.itaysonlab.jetispot.ui.screens.auth.AuthScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.ConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.NormalizationConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.QualityConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.StorageScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.dac.DacRendererScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.dynamic.DynamicSpIdScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.search.SearchScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2.YourLibraryContainerScreen
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import soup.compose.material.motion.navigation.composable

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
  navController: NavHostController,
  sessionManager: SpSessionManager,
  authManager: SpAuthManager,
  modifier: Modifier
) {
  val slideDistance = rememberSlideDistance()

  LaunchedEffect(Unit) {
    if (sessionManager.isSignedIn()) return@LaunchedEffect
    authManager.authStored()
    navController.navigate(if (sessionManager.isSignedIn()) Screen.Feed.route else Screen.Authorization.route) {
      popUpTo(Screen.NavGraph.route)
    }
  }

  MaterialMotionNavHost(
    navController = navController,
    startDestination = Screen.CoreLoading.route,
    route = Screen.NavGraph.route,
    modifier = modifier,
    enterTransition = {
      materialSharedAxisXIn(forward = true, slideDistance)
    }, exitTransition = {
      materialSharedAxisXOut(forward = true, slideDistance)
    }, popEnterTransition = {
      materialSharedAxisXIn(forward = false, slideDistance)
    }, popExitTransition = {
      materialSharedAxisXOut(forward = false, slideDistance)
    }
  ) {
    composable(Screen.CoreLoading.route) {
      Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier
          .align(Alignment.Center)
          .size(56.dp))
      }
    }

    composable(Screen.Authorization.route) {
      AuthScreen()
    }

    composable(Screen.Feed.route) {
      DacRendererScreen("", true, {
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
      DynamicSpIdScreen(uri, "spotify:$uri")
    }

    composable(Screen.DacViewCurrentPlan.route) {
      DacRendererScreen(stringResource(id = Screen.DacViewCurrentPlan.title), false, {
        getPlanOverview()
      })
    }

    composable(Screen.DacViewAllPlans.route) {
      DacRendererScreen(stringResource(id = Screen.DacViewAllPlans.title), false, {
        getAllPlans()
      })
    }

    composable(Screen.Config.route) { ConfigScreen() }
    composable(Screen.StorageConfig.route) { StorageScreen() }
    composable(Screen.QualityConfig.route) { QualityConfigScreen() }
    composable(Screen.NormalizationConfig.route) { NormalizationConfigScreen() }
    composable(Screen.Search.route) { SearchScreen() }
    composable(Screen.Library.route) { YourLibraryContainerScreen() }

    dialog(Dialog.AuthDisclaimer.route) {
      AlertDialog(onDismissRequest = { navController.popBackStack() }, icon = {
        Icon(Icons.Rounded.Warning, null)
      }, title = {
        Text(stringResource(id = R.string.auth_disclaimer))
      }, text = {
        Text(stringResource(id = R.string.auth_disclaimer_text))
      }, confirmButton = {
        TextButton(onClick = { navController.popBackStack() }) {
          Text(stringResource(id = R.string.logout_confirm))
        }
      })
    }

    dialog(Dialog.Logout.route) {
      AlertDialog(onDismissRequest = { navController.popBackStack() }, icon = {
        Icon(Icons.Rounded.Warning, null)
      }, title = {
        Text(stringResource(id = R.string.logout_title))
      }, text = {
        Text(stringResource(id = R.string.logout_message))
      }, confirmButton = {
        TextButton(onClick = {
          navController.popBackStack()
          authManager.reset()
          android.os.Process.killProcess(android.os.Process.myPid()) // TODO: maybe dynamic restart the session instances?
        }) {
          Text(stringResource(id = R.string.logout_confirm))
        }
      }, dismissButton = {
        TextButton(onClick = { navController.popBackStack() }) {
          Text(stringResource(id = R.string.logout_cancel))
        }
      })
    }

    bottomSheet(BottomSheet.JumpToArtist.route) { entry ->
      val data = remember { entry.arguments!!.getString("artistIdsAndRoles")!! }
      JumpToArtistBottomSheet(data = data)
    }
  }
}