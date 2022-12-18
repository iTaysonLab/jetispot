package bruhcollective.itaysonlab.jetispot.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.*
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.SpApp.Companion.context
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.util.UpdateUtil
import bruhcollective.itaysonlab.jetispot.ui.bottomsheets.MoreOptionsBottomSheet
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
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.screens.search.SearchScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2.YourLibraryContainerScreen
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun AppNavigation(
  navController: NavHostController,
  sessionManager: SpSessionManager,
  authManager: SpAuthManager,
  modifier: Modifier
) {

  var showUpdateDialog by rememberSaveable { mutableStateOf(false) }
  var currentDownloadStatus by remember { mutableStateOf(UpdateUtil.DownloadStatus.NotYet as UpdateUtil.DownloadStatus) }
  val scope = rememberCoroutineScope()
  var updateJob: Job? = null
  var latestRelease by remember { mutableStateOf(UpdateUtil.LatestRelease()) }
  val settings =
    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      UpdateUtil.installLatestApk()
    }
  val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { result ->
    if (result) {
      UpdateUtil.installLatestApk()
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (!context.packageManager.canRequestPackageInstalls())
          settings.launch(
            Intent(
              Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
              Uri.parse("package:${context.packageName}"),
            )
          )
        else
          UpdateUtil.installLatestApk()
      }
    }
  }

  LaunchedEffect(Unit) {
    if (sessionManager.isSignedIn()) return@LaunchedEffect
    authManager.authStored()
    navController.navigate(if (sessionManager.isSignedIn()) Screen.Feed.route else Screen.Authorization.route) {
      popUpTo(Screen.NavGraph.route)
    }
  }
  LaunchedEffect(Unit){
    launch(Dispatchers.IO) {
      kotlin.runCatching {
        val temp = UpdateUtil.checkForUpdate()
        if (temp != null) {
          latestRelease = temp
          showUpdateDialog = true
        }
      }.onFailure {
        it.printStackTrace()
      }
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
    composable(Screen.Search.route) { SearchScreen() }
    composable(Screen.Library.route) { YourLibraryContainerScreen() }

    //DIALOGS

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

    //Bottom Sheet Dialogs

    bottomSheet(BottomSheet.JumpToArtist.route) { entry ->
      val data = remember { entry.arguments!!.getString("artistIdsAndRoles")!! }
      JumpToArtistBottomSheet(data = data)
    }

    bottomSheet(BottomSheet.MoreOptions.route) { entry ->
      val trackName = remember {entry.arguments!!.getString("trackName")!!}
      val artistName = remember {entry.arguments!!.getString("artistName")!!}
      val artworkUrl = remember {entry.arguments!!.getString("artworkUrl")!!}
      MoreOptionsBottomSheet(trackName = trackName, artistName = artistName, artworkUrl = artworkUrl)
    }
  }

  if (showUpdateDialog) {
    UpdateDialog(
      onDismissRequest = {
        showUpdateDialog = false
        updateJob?.cancel()
      },
      title = latestRelease.name.toString(),
      onConfirmUpdate = {
        updateJob = scope.launch(Dispatchers.IO) {
          kotlin.runCatching {
            UpdateUtil.downloadApk(latestRelease = latestRelease)
              .collect { downloadStatus ->
                currentDownloadStatus = downloadStatus
                if (downloadStatus is UpdateUtil.DownloadStatus.Finished) {
                  launcher.launch(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                }
              }
          }.onFailure {
            it.printStackTrace()
            currentDownloadStatus = UpdateUtil.DownloadStatus.NotYet
            //TODO: Show error
            return@launch
          }
        }
      },
      releaseNote = latestRelease.body.toString(),
      downloadStatus = currentDownloadStatus
    )
  }
}


@Composable
fun UpdateDialog(
  onDismissRequest: () -> Unit,
  title: String,
  onConfirmUpdate: () -> Unit,
  releaseNote: String,
  downloadStatus: UpdateUtil.DownloadStatus,
) {
  AlertDialog(
    onDismissRequest = {},
    title = {
      Text(title)

    },
    icon = { Icon(Icons.Outlined.NewReleases, null) }, confirmButton = {
      TextButton(onClick = { if (downloadStatus !is UpdateUtil.DownloadStatus.Progress) onConfirmUpdate() }) {
        when (downloadStatus) {
          is UpdateUtil.DownloadStatus.Progress -> Text("${downloadStatus.percent} %")
          else -> Text(stringResource(R.string.update))
        }
      }
    }, dismissButton = {
      DismissButton { onDismissRequest() }
    }, text = {
      Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(releaseNote)
      }
    })
}

@Composable
fun DismissButton(text: String = stringResource(R.string.dismiss), onClick: () -> Unit) {
  TextButton(onClick = onClick) {
    Text(text)
  }
}