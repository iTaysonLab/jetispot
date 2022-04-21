package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun NowPlayingScreen (
  navController: NavController,
  bottomSheetState: BottomSheetState,
  viewModel: NowPlayingViewModel = hiltViewModel()
) {
  Surface(tonalElevation = 16.dp, modifier = Modifier.fillMaxSize()) {

  }
}

@Composable
fun NowPlayingHeader (

) {

}

@Composable
fun NowPlayingControls (

) {

}

@Composable
fun NowPlayingBackground (

) {

}

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
  private val spPlayerServiceManager: SpPlayerServiceManager
): ViewModel() {
  val currentTrack get() = spPlayerServiceManager.currentTrack
}