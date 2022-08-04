package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
  val artworkSize = animateDpAsState(
    48.dp + (bsOffset * LocalConfiguration.current.screenWidthDp).dp,
    animationSpec = spring()
  ).value
  val artworkX = animateDpAsState(
    bsOffset.dp,
    animationSpec = spring()
  ).value
  val artworkY = animateDpAsState(
    with(LocalDensity.current) { (1f + ((bsOffset * -70) * (LocalConfiguration.current.screenHeightDp / -215f))).toDp() },
    animationSpec = spring()
  ).value
  val artworkPaddingStart = animateDpAsState((16f * (1f - bsOffset)).dp).value
  val artworkPaddingTop = animateDpAsState((8f - bsOffset).dp).value
  val animatedCorners = animateDpAsState((8 + (24f * bsOffset)).dp).value

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
    Row(Modifier.padding(start = artworkPaddingStart, top = artworkPaddingTop)) {
      Surface(
        color = Color.Transparent,
        modifier = Modifier
          .size(artworkSize)
          .align(Alignment.Top)
          .absoluteOffset(
            x = artworkX,
            y = artworkY
          )
      ) {
        ArtworkPager(viewModel, mainPagerState, animatedCorners)
      }
    }

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
        
        Column(Modifier.size((LocalConfiguration.current.screenWidthDp * 0.9).dp)) { }

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