package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun NowPlayingFullscreenComposition (
  bottomSheetState: BottomSheetState,
  mainPagerState: PagerState,
  viewModel: NowPlayingViewModel
) {
  val scope = rememberCoroutineScope()

  Box(modifier = Modifier.fillMaxSize()) {
    NowPlayingBackground(
      state = mainPagerState,
      viewModel = viewModel,
      modifier = Modifier.fillMaxSize(),
    )

    // main content
    NowPlayingHeader(
      stateTitle = stringResource(id = viewModel.getHeaderTitle()),
      onCloseClick = {
        scope.launch { bottomSheetState.collapse() }
      },
      state = viewModel.getHeaderText(),
      modifier = Modifier
        .statusBarsPadding()
        .align(Alignment.TopCenter)
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    )

    NowPlayingControls(
      scope = scope, viewModel = viewModel, bottomSheetState = bottomSheetState, modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(horizontal = 8.dp)
        .padding(bottom = 24.dp)
        .navigationBarsPadding()
    )
  }
}