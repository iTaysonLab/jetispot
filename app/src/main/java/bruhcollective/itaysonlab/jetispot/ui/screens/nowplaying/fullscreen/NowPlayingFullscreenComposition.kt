package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.ext.disableTouch
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.theme.ApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NowPlayingFullscreenComposition(
    queueOpened: Boolean,
    setQueueOpened: (Boolean) -> Unit,
    lyricsOpened: Boolean,
    setLyricsOpened: (Boolean) -> Unit,
    bottomSheetState: BottomSheetState,
    bsOffset: Float,
    viewModel: NowPlayingViewModel
) {
    val scope = rememberCoroutineScope()

    val queueProgress = animateFloatAsState(
        targetValue = if (queueOpened) 1f else 0f,
        animationSpec = spring(stiffness = 450f)
    )
    val queueProgressValue = queueProgress.value

    val lyricsProgress = animateFloatAsState(
        targetValue = if (lyricsOpened) 1f else 0f,
        animationSpec = spring(stiffness = 450f)
    )
    val lyricsProgressValue = lyricsProgress.value

    val anySuperProgress = remember(queueProgressValue, lyricsProgressValue) {
        if (queueProgressValue > 0f) {
            queueProgressValue
        } else {
            lyricsProgressValue
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            MaterialTheme.colorScheme.compositeSurfaceElevation(
                3.dp
            )
        )) {

        NowPlayingBackground(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .alpha(1f * bsOffset),
        )

        // main content
        AnimatedVisibility(visible = bsOffset >= 0.99f, enter = fadeIn(), exit = fadeOut()) {
            NowPlayingHeader(
                stateTitle = stringResource(id = viewModel.getHeaderTitle()),
                onCloseClick = {
                    if (lyricsOpened) {
                        setLyricsOpened(false)
                    } else if (queueOpened) {
                        setQueueOpened(false)
                    } else {
                        scope.launch { bottomSheetState.collapse() }
                    }
                },
                state = viewModel.getHeaderText(LocalContext.current),
                queueStateProgress = anySuperProgress,
                modifier = Modifier
                    .statusBarsPadding()
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                viewModel = viewModel,
                bottomSheetState = bottomSheetState,
                scope = scope,
            )
        }
        // composite

        if (anySuperProgress != 1f) {
            NowPlayingControls(
                scope = scope,
                viewModel = viewModel,
                bottomSheetState = bottomSheetState,
                queueOpened = queueOpened,
                setQueueOpened = setQueueOpened,
                lyricsOpened = lyricsOpened,
                setLyricsOpened = setLyricsOpened,
                modifier = Modifier
                    .disableTouch(disabled = queueOpened)
                    .alpha(1f - anySuperProgress)
                    .align(Alignment.BottomStart)
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding()
                    .offset {
                        IntOffset(x = 0, y = (-(48).dp.toPx() * (anySuperProgress)).toInt())
                    }
            )
        }

        NowPlayingQueue(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .alpha(1f - lyricsProgressValue),
            rvStateProgress = queueProgressValue
        )

        NowPlayingLyricsComposition(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .alpha(1f - queueProgressValue),
            rvStateProgress = lyricsProgressValue
        )
    }
}