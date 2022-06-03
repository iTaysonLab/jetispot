package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen.NowPlayingFullscreenComposition
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
fun NowPlayingScreen(
  navController: NavController,
  bottomSheetState: BottomSheetState,
  bsOffset: () -> Float,
  viewModel: NowPlayingViewModel = hiltViewModel()
) {
  val scope = rememberCoroutineScope()
  val mainPagerState = rememberPagerState()

  LaunchedEffect(Unit) {
    // one-time VM-UI connection
    viewModel.uiOnTrackIndexChanged = { new ->
      scope.launch { mainPagerState.animateScrollToPage(new) }
    }
  }

  Box(Modifier.fillMaxSize()) {
    NowPlayingFullscreenComposition(
      bottomSheetState = bottomSheetState,
      mainPagerState = mainPagerState,
      viewModel = viewModel
    )

    NowPlayingMiniplayer(
      viewModel,
      Modifier
        .clickable { scope.launch { bottomSheetState.expand() } }
        .fillMaxWidth()
        .height(72.dp)
        .align(Alignment.TopStart)
        .alpha(1f - bsOffset())
    )
  }
}