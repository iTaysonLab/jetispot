package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
  viewModel: NowPlayingViewModel,
  bsOffset: Float
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
    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
      // main content
      Column(
        modifier = Modifier
          .alpha(bsOffset)
          .navigationBarsPadding()
          .fillMaxHeight()
          .padding(top = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        NowPlayingHeader(
          stateTitle = stringResource(id = viewModel.getHeaderTitle()),
          onCloseClick = { scope.launch { bottomSheetState.collapse() } },
          state = viewModel.getHeaderText(),
          modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        )

        Column(modifier = Modifier.padding(vertical = 16.dp)) {
          ArtworkPager(viewModel, mainPagerState)
        }


        Column(Modifier.padding(horizontal = 8.dp)) {
          ControlsHeader(scope, navController, bottomSheetState, viewModel)
          ControlsSeekbar(viewModel)
        }

        ControlsMainButtons(viewModel)

        ControlsBottomAccessories(viewModel)
      }
    }
  }
}