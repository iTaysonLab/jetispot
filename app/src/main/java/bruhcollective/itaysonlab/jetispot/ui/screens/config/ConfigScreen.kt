package bruhcollective.itaysonlab.jetispot.ui.screens.config

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.BuildConfig
import bruhcollective.itaysonlab.jetispot.core.SpConfigurationManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen (
  navController: NavController,
  viewModel: ConfigScreenViewModel = hiltViewModel()
) {
  Scaffold(topBar = {
    SmallTopAppBar(title = {
       Text("Configuration")
    }, Modifier.statusBarsPadding())
  }) { innerPadding ->
    Column(Modifier.padding(innerPadding)) {
      ConfigCategory("Playback")
      ConfigPreference(title = "Audio quality", subtitle = "Very high")
      ConfigPreference(title = "Normalization", subtitle = "Balanced")
      ConfigPreference(title = "Crossfade", subtitle = "Disabled")
      ConfigSwitch(text = "Autoplay suggested tracks", value = true)
      ConfigSwitch(text = "Preload track data", value = false)
      ConfigCategory("Account")
      ConfigPreference(title = "Logout")
      ConfigCategory("About")
      ConfigPreference(title = "Jetispot", subtitle = "version ${BuildConfig.VERSION_NAME}")
      ConfigPreference(title = "Open Telegram channel")
      ConfigPreference(title = "View source code")
      ConfigPreference(title = "Used OSS libraries")
    }
  }
}

//

@Composable
@Stable
fun ConfigCategory (
  text: String
) {
  Text(text = text, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
}

@Composable
@Stable
fun ConfigSwitch (
  text: String,
  value: Boolean
) {
  Row(modifier = Modifier
    .fillMaxWidth()
    .clickable {

    }
    .padding(horizontal = 16.dp, vertical = 8.dp)) {
    Text(text = text, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.CenterVertically))
    Switch(checked = value, onCheckedChange = {}, modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically))
  }
}

@Composable
fun ConfigPreference (
  title: String,
  subtitle: String = ""
) {
  Column(modifier = Modifier
    .fillMaxWidth()
    .clickable {

    }
    .padding(horizontal = 16.dp, vertical = 16.dp)) {
    Text(text = title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp)
    if (subtitle.isNotEmpty()) Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
  }
}

//

@HiltViewModel
class ConfigScreenViewModel @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val spConfigurationManager: SpConfigurationManager
): ViewModel() {

}