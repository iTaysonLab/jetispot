package bruhcollective.itaysonlab.jetispot.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import bruhcollective.itaysonlab.jetispot.ui.shared.AppPreferences.NPAnimationDamping
import bruhcollective.itaysonlab.jetispot.ui.shared.AppPreferences.NPAnimationStiffness
import bruhcollective.itaysonlab.jetispot.ui.shared.AppPreferences.ColorScheme
import bruhcollective.itaysonlab.jetispot.ui.shared.colorSchemePreviewBoxV1
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun AppNavigation(
  navController: NavHostController,
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
    composable(Screen.Search.route) { BrowseRootScreen() }
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
          Text(stringResource(id = R.string.cancel))
        }
      })
    }

    dialog(Dialog.ColorSelect.route){
      AlertDialog(
        onDismissRequest = { navController.popBackStack() },
        title = { Text(stringResource(id = R.string.config_color_theme), textAlign = TextAlign.Center) },

        text = {
          Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(id = R.string.theme_reload_message), textAlign = TextAlign.Center)

            Row(Modifier.align(Alignment.CenterHorizontally)){
              colorSchemePreviewBoxV1(SchemeColor = "#1DB954", onClick = { ColorScheme = "#1DB954" }) // TODO: maybe kill all activity and start MainActivity?
              colorSchemePreviewBoxV1(SchemeColor = "#134D2B", onClick = { ColorScheme = "#134D2B" })
              colorSchemePreviewBoxV1(SchemeColor = "#4DA818", onClick = { ColorScheme = "#4DA818" })
              colorSchemePreviewBoxV1(SchemeColor = "#A1A818", onClick = { ColorScheme = "#A1A818" })
            }

            Row(Modifier.align(Alignment.CenterHorizontally)){
              colorSchemePreviewBoxV1(SchemeColor = "#EB4034", onClick = { ColorScheme = "#EB4034" })
              colorSchemePreviewBoxV1(SchemeColor = "#B60A0D", onClick = { ColorScheme = "#B60A0D" })
              colorSchemePreviewBoxV1(SchemeColor = "#6E1E32", onClick = { ColorScheme = "#6E1E32" })
              colorSchemePreviewBoxV1(SchemeColor = "#B60A86", onClick = { ColorScheme = "#B60A86" })
            }

            Row(Modifier.align(Alignment.CenterHorizontally)){
              colorSchemePreviewBoxV1(SchemeColor = "#056786", onClick = { ColorScheme = "#056786" })
              colorSchemePreviewBoxV1(SchemeColor = "#009182", onClick = { ColorScheme = "#009182" })
            }
          }
        },
        confirmButton = {
          Row(Modifier.padding(all = 8.dp), horizontalArrangement = Arrangement.Center) {
            Button(modifier = Modifier.fillMaxWidth(), onClick = { navController.popBackStack() }
            ) {
              Text(stringResource(id = R.string.cancel))
            }
          }
        }
      )
    }

    dialog(Dialog.SetArtworkData.route) {
      AlertDialog(
        onDismissRequest = { navController.popBackStack() },
        title = { Text(stringResource(id = R.string.config_edit_animation), textAlign = TextAlign.Start) },
        text = {
          Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            var sliderData by remember { mutableStateOf(0f) }
            Text("Damping: $sliderData", textAlign = TextAlign.Center)
            Slider(
              value = sliderData,
              onValueChange = {
                val roundedData = (it * 100).roundToInt().toFloat() / 100
                sliderData = roundedData
                NPAnimationDamping = roundedData
              },
              steps = 19,
              valueRange = 0f..10f
            )

            var sliderData2 by remember { mutableStateOf(NPAnimationStiffness!!) }
            Text("Stiffness: $sliderData2", textAlign = TextAlign.Center)
            Slider(
              value = sliderData2,
              onValueChange = {
                val roundedData = (it * 100).roundToInt().toFloat() / 100
                sliderData2 =  roundedData
                NPAnimationStiffness = roundedData
              },
              steps = 19
            )
          }
        },
        confirmButton = {
          Row(
            Modifier
              .padding(all = 8.dp)
              .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {NPAnimationDamping = 0.75f; NPAnimationStiffness = 0.6f}
            ) {
              Text(stringResource(id = R.string.edit_animation_set_default))
            }
            Spacer(Modifier.width(6.dp))
            Button(onClick = { navController.popBackStack() }
            ) {
              Text(stringResource(id = R.string.cancel))
            }
          }
        }
      )
    }

    bottomSheet(BottomSheet.JumpToArtist.route) { entry ->
      val data = remember { entry.arguments!!.getString("artistIdsAndRoles")!! }
      JumpToArtistBottomSheet(data = data)
    }
  }
}