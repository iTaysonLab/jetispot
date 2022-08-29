package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import bruhcollective.itaysonlab.jetispot.ui.ext.blendWith
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.theme.ApplicationTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme.colorScheme as monet

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun NowPlayingFullscreenComposition (
  queueOpened: Boolean,
  setQueueOpened: (Boolean) -> Unit,
  bottomSheetState: BottomSheetState,
  mainPagerState: PagerState,
  viewModel: NowPlayingViewModel,
  bsOffset: Float
) {
  val scope = rememberCoroutineScope()


  Box(modifier = Modifier
    .fillMaxSize()
    .background(
      if (isSystemInDarkTheme())
        animateColorAsState(
          monet.surface.blendWith(monet.primary, ratio = 0.05f),
          tween(durationMillis = 500)
        ).value
      else
        animateColorAsState(
          monet.surface.blendWith(monet.primary, ratio = 0.1f),
          tween(durationMillis = 500)
        ).value
    )
  ) {
    ApplicationTheme() {
      Box(modifier = Modifier.alpha(1f - bsOffset).fillMaxSize().background(monet.compositeSurfaceElevation(3.dp)))
    }

    Row(
      Modifier
        .padding(
          start = animateDpAsState((16f * (1f - bsOffset)).dp).value,
          top = animateDpAsState((8f - bsOffset).dp).value
        )
    ) {
      Surface(
        color = Color.Transparent,
        modifier = Modifier
          .size(
            animateDpAsState(
              48.dp + (bsOffset * LocalConfiguration.current.screenWidthDp).dp,
              animationSpec = spring()
            ).value
          )
          .align(Alignment.Top)
          .absoluteOffset(
            x =
            animateDpAsState(
              bsOffset.dp,
              animationSpec = spring()
            ).value,
            y = animateDpAsState(
              with(LocalDensity.current) { (1f + ((bsOffset * -70) * (LocalConfiguration.current.screenHeightDp / -215f))).toDp() },
              animationSpec = spring()
            ).value
          )
      ) {
        ArtworkPager(viewModel, mainPagerState, bsOffset)
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
          onCloseClick = {
            if (queueOpened)
              setQueueOpened(false)
            else
              scope.launch { bottomSheetState.collapse() }
          },
          queueStateProgress = animateFloatAsState(
            targetValue = if (queueOpened) 1f else 0f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
          ).value,
          state = viewModel.getHeaderText(),
          modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        )

        Column(Modifier.size((LocalConfiguration.current.screenWidthDp * 0.9).dp)) { }

        Column(Modifier.padding(horizontal = 8.dp)) {
          ControlsHeader(scope, bottomSheetState, viewModel)
          ControlsSeekbar(viewModel)
        }

        ControlsMainButtons(viewModel, queueOpened, setQueueOpened)

        ControlsBottomAccessories(viewModel, queueOpened, setQueueOpened)
      }
    }

    NowPlayingQueue(
      viewModel = viewModel,
      modifier = Modifier.fillMaxSize(),
      rvStateProgress = animateFloatAsState(
        targetValue = if (queueOpened) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing)
      ).value
    )
  }
}