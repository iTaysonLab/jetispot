package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen.NowPlayingFullscreenComposition
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun NowPlayingScreen(
    bottomSheetState: BottomSheetState,
    bsOffset: () -> Float,
    queueOpened: Boolean,
    setQueueOpened: (Boolean) -> Unit,
    lyricsOpened: Boolean,
    setLyricsOpened: (Boolean) -> Unit,
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        NowPlayingFullscreenComposition(
            queueOpened = queueOpened,
            setQueueOpened = setQueueOpened,
            lyricsOpened = lyricsOpened,
            setLyricsOpened = setLyricsOpened,
            bottomSheetState = bottomSheetState,
            viewModel = viewModel,
            bsOffset = bsOffset()
        )

        NowPlayingMiniplayer(
            viewModel,
            Modifier
                .alpha(1f - bsOffset())
                .clickable { scope.launch { bottomSheetState.expand() } }
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.TopStart),
            visible = bsOffset() <= 0.99f,
            bsOffset = bsOffset()
        )
    }
}
