package bruhcollective.itaysonlab.jetispot.ui

import android.content.Intent
import android.net.Uri
import android.os.Process.killProcess
import android.os.Process
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.BrowseRootScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2.YourLibraryContainerScreen
import bruhcollective.itaysonlab.jetispot.ui.shared.AppPreferences
import bruhcollective.itaysonlab.jetispot.ui.shared.colorSchemePreviewBoxV1
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

@OptIn(ExperimentalMaterialNavigationApi::class)
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
    navController.navigate(
      if (sessionManager.isSignedIn())
        Screen.Feed.route
      else
        Screen.Authorization.route
    ) {
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

    composable(Screen.Authorization.route) { AuthScreen(provideLambdaController) }

    composable(Screen.Feed.route) {
      DacRendererScreen(
        provideLambdaController,
        "",
        true,
        { getDacHome(SpInternalApi.buildDacRequestForHome(it)) }
      )
    }

    composable(
      Screen.SpotifyIdRedirect.route,
      deepLinks = listOf(
        navDeepLink {
          uriPattern = Screen.deeplinkCapable[Screen.SpotifyIdRedirect]
          action = Intent.ACTION_VIEW
        }
      )
    ) {
      val fullUrl = it.arguments?.getString("uri")
      val dpLinkType = it.arguments?.getString("type")
      val dpLinkTypeId = it.arguments?.getString("typeId")
      val uri = fullUrl ?: "$dpLinkType:$dpLinkTypeId"
      DynamicSpIdScreen(provideLambdaController, uri, "spotify:$uri")
    }

    composable(Screen.DacViewCurrentPlan.route) {
      DacRendererScreen(
        provideLambdaController,
        stringResource(id = Screen.DacViewCurrentPlan.title),
        false,
        { getPlanOverview() }
      )
    }

    composable(Screen.DacViewAllPlans.route) {
      DacRendererScreen(
        provideLambdaController,
        stringResource(id = Screen.DacViewAllPlans.title),
        false,
        { getAllPlans() }
      )
    }

    composable(Screen.Config.route) { ConfigScreen(provideLambdaController) }
    composable(Screen.StorageConfig.route) { StorageScreen() }
    composable(Screen.QualityConfig.route) { QualityConfigScreen(provideLambdaController) }
    composable(Screen.NormalizationConfig.route) { NormalizationConfigScreen(provideLambdaController) }
    composable(Screen.Search.route) { BrowseRootScreen(provideLambdaController) }
    composable(Screen.Library.route) { YourLibraryContainerScreen(provideLambdaController) }

    dialog(Dialog.AuthDisclaimer.route) {
      AlertDialog(onDismissRequest = { navController.popBackStack() },
        icon = { Icon(Icons.Rounded.Warning, null) },
        title = { Text(stringResource(id = R.string.auth_disclaimer)) },
        text = { Text(stringResource(id = R.string.auth_disclaimer_text)) },
        confirmButton = { TextButton(onClick = { navController.popBackStack() }) {
          Text(stringResource(id = R.string.logout_confirm)) }
        }
      )
    }

    dialog(Dialog.Logout.route) {
      AlertDialog(
        onDismissRequest = { navController.popBackStack() },
        icon = { Icon(Icons.Rounded.Warning, null) },
        title = { Text(stringResource(id = R.string.logout_title)) },
        text = { Text(stringResource(id = R.string.logout_message)) },
        confirmButton = {
          TextButton(
            onClick = {
              navController.popBackStack()
              authManager.reset()
              killProcess(Process.myPid()) // TODO: maybe dynamic restart the session instances?
            }
          ) { Text(stringResource(id = R.string.logout_confirm)) }
      },
        dismissButton = {
          TextButton(onClick = { navController.popBackStack() }) {
            Text(stringResource(id = R.string.logout_cancel))
          }
        }
      )
    }

  dialog(Dialog.ColorSelect.route){
    AlertDialog(
      onDismissRequest = {
        navController.popBackStack()
      },
      title = {
        Text(stringResource(id = R.string.color_theme), textAlign = TextAlign.Center)
      },

      text = {
        Column(
          Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ){
          Text(stringResource(id = R.string.reload_message), textAlign = TextAlign.Center)
          Row(Modifier.align(Alignment.CenterHorizontally)){
            colorSchemePreviewBoxV1(SchemeColor = "#1DB954", onClick = { AppPreferences.ColorScheme = "#1DB954" }) // TODO: maybe kill all activity and start MainActivity?
            colorSchemePreviewBoxV1(SchemeColor = "#134D2B", onClick = { AppPreferences.ColorScheme = "#134D2B" })
            colorSchemePreviewBoxV1(SchemeColor = "#4DA818", onClick = { AppPreferences.ColorScheme = "#4DA818" })
            colorSchemePreviewBoxV1(SchemeColor = "#A1A818", onClick = { AppPreferences.ColorScheme = "#A1A818" })
          }
          Row(Modifier.align(Alignment.CenterHorizontally)){
            colorSchemePreviewBoxV1(SchemeColor = "#EB4034", onClick = { AppPreferences.ColorScheme = "#EB4034" })
            colorSchemePreviewBoxV1(SchemeColor = "#B60A0D", onClick = { AppPreferences.ColorScheme = "#B60A0D" })
            colorSchemePreviewBoxV1(SchemeColor = "#6E1E32", onClick = { AppPreferences.ColorScheme = "#6E1E32" })
            colorSchemePreviewBoxV1(SchemeColor = "#B60A86", onClick = { AppPreferences.ColorScheme = "#B60A86" })
          }
          Row(Modifier.align(Alignment.CenterHorizontally)){
            colorSchemePreviewBoxV1(SchemeColor = "#056786", onClick = { AppPreferences.ColorScheme = "#056786" })
            colorSchemePreviewBoxV1(SchemeColor = "#009182", onClick = { AppPreferences.ColorScheme = "#009182" })
          }
        }
      },
      confirmButton = {
        Row(
          modifier = Modifier.padding(all = 8.dp),
          horizontalArrangement = Arrangement.Center
        ) {
          Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.popBackStack() }
          ) {
            Text(stringResource(id = R.string.dismiss))
          }
        }
      }
    )
  }
    bottomSheet(BottomSheet.JumpToArtist.route) { entry ->
      val data = remember { entry.arguments!!.getString("artistIdsAndRoles")!! }
      JumpToArtistBottomSheet(navController = provideLambdaController, data = data)
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

  fun navigate(sheet: BottomSheet, args: Map<String, String>) {
    var url = sheet.route

    args.forEach { entry ->
      url = url.replace("{${entry.key}}", entry.value)
    }

    controller().navigate(url)
  }

  fun navigateAndClearStack(screen: Screen) = controller().navigate(screen.route) { popUpTo(Screen.NavGraph.route) }

  fun popBackStack() = controller().popBackStack()

  fun context() = controller().context
  fun string(@StringRes id: Int) = context().getString(id)
  fun openInBrowser(uri: String) = context().startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri)))
}
