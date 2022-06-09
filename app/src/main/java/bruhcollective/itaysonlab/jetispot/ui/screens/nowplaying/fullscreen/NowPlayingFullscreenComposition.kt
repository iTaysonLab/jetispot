package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.ext.blendWith
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme.colorScheme as monet

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun NowPlayingFullscreenComposition (
  navController: LambdaNavigationController,
  bottomSheetState: BottomSheetState,
  mainPagerState: PagerState,
  viewModel: NowPlayingViewModel
) {
  val scope = rememberCoroutineScope()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        if (isSystemInDarkTheme())
          monet.surface.blendWith(monet.primary, ratio = 0.05f)
        else
          monet.primary.copy(0.1f)
      )
  ) {
    Column {
      // main content
      NowPlayingHeader(
        stateTitle = stringResource(id = viewModel.getHeaderTitle()),
        onCloseClick = {
          scope.launch { bottomSheetState.collapse() }
        },
        state = viewModel.getHeaderText(),
        modifier = Modifier
          .statusBarsPadding()
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )

      NowPlayingControls(
        scope = scope,
        viewModel = viewModel,
        navController = navController,
        bottomSheetState = bottomSheetState,
        modifier = Modifier
          .padding(bottom = 0.dp)
          .navigationBarsPadding()
          .fillMaxHeight(),
        pagerState = mainPagerState
      )
    }
  }
}