package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.*
import com.spotify.metadata.Metadata
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NowPlayingControls(
    scope: CoroutineScope,
    queueOpened: Boolean,
    setQueueOpened: (Boolean) -> Unit,
    lyricsOpened: Boolean,
    setLyricsOpened: (Boolean) -> Unit,
    bottomSheetState: BottomSheetState,
    viewModel: NowPlayingViewModel,
    modifier: Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.Bottom) {
        ControlsArtwork(viewModel)
        Spacer(Modifier.height(16.dp))
        ControlsHeader(scope, bottomSheetState, viewModel)
        Spacer(Modifier.height(16.dp))
        ControlsMainButtons(viewModel, queueOpened, setQueueOpened)
        Spacer(Modifier.height(12.dp))
        ControlsSeekbar(viewModel)
        Spacer(Modifier.height(12.dp))
        NowPlayingLyricsContainer(viewModel, lyricsOpened, setLyricsOpened)
    }
}

@Composable
private fun ControlsArtwork(
    viewModel: NowPlayingViewModel,
) {
    ElevatedCard(modifier = Modifier
        .padding(horizontal = 14.dp)
        .clip(RoundedCornerShape(12.dp))) {
        PreviewableAsyncImage(
            imageUrl = remember(viewModel.currentTrack.value) {
                if (viewModel.currentQueue.value.isNotEmpty()) {
                    SpUtils.getImageUrl(viewModel.currentQueue.value[viewModel.currentQueuePosition.value].album.coverGroup.imageList.find { it.size == Metadata.Image.Size.LARGE }?.fileId)
                } else {
                    null
                }
            },
            placeholderType = "track",
            modifier = Modifier
                .size(128.dp)

        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ControlsHeader(
    scope: CoroutineScope,
    bottomSheetState: BottomSheetState,
    viewModel: NowPlayingViewModel,
) {
    MarqueeText(
        text = viewModel.currentTrack.value.title,
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .navClickable(
                enableRipple = false
            ) { navController ->
                viewModel.navigateToSource(scope, bottomSheetState, navController)
            },
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
    )
    
    Spacer(Modifier.height(2.dp))

    MarqueeText(text = viewModel.currentTrack.value.artist,
        modifier = Modifier
            .alpha(0.7f)
            .padding(horizontal = 14.dp)
            .navClickable(
                enableRipple = false
            ) { navController ->
                viewModel.navigateToArtist(scope, bottomSheetState, navController)
            },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontSize = 16.sp,
    )
}

@Composable
private fun ControlsSeekbar(
    viewModel: NowPlayingViewModel,
) {
    var isSeekbarDragging by remember { mutableStateOf(false) }
    var seekbarDraggingProgress by remember { mutableStateOf(0f) }

    val elapsedTime = remember(viewModel.currentPosition.value, isSeekbarDragging, seekbarDraggingProgress) {
        val ms = if (isSeekbarDragging) {
            (seekbarDraggingProgress * viewModel.currentTrack.value.duration).toLong()
        } else {
            viewModel.currentPosition.value.progressMilliseconds
        } / 1000L

        DateUtils.formatElapsedTime(ms)
    }

    val totalTime = remember(viewModel.currentPosition.value) {
        DateUtils.formatElapsedTime(viewModel.currentTrack.value.duration / 1000L)
    }

    Slider(value = if (isSeekbarDragging) seekbarDraggingProgress else viewModel.currentPosition.value.progressRange, colors = SliderDefaults.colors(
        thumbColor = oppositeColorOfSystem(alpha = 1f),
        activeTrackColor = oppositeColorOfSystem(alpha = 0.7f),
        inactiveTrackColor = oppositeColorOfSystem(alpha = 0.35f)
    ), onValueChange = {
        isSeekbarDragging = true
        seekbarDraggingProgress = it
    }, onValueChangeFinished = {
        isSeekbarDragging = false
        viewModel.seekTo((seekbarDraggingProgress * viewModel.currentTrack.value.duration).toLong())
    }, modifier = Modifier.padding(horizontal = 8.dp))

    Row(
        Modifier
            .padding(horizontal = 14.dp)
            .offset(y = (-6).dp)
    ) {
        Text(
            text = elapsedTime,
            modifier = Modifier.alpha(0.7f),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = totalTime,
            fontSize = 12.sp,
            modifier = Modifier.alpha(0.7f)
        )
    }
}

@Composable
private fun ControlsMainButtons(
    viewModel: NowPlayingViewModel,
    queueOpened: Boolean,
    setQueueOpened: (Boolean) -> Unit,
) {
    Row(Modifier.padding(horizontal = 14.dp)) {
        Surface(color = oppositeColorOfSystem(if (!isSystemInDarkTheme()) 0.75f else 1f), modifier = Modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = systemThemeColor(alpha = 1f))
            ) {
                viewModel.togglePlayPause()
            }) {
            PlayPauseButton(
                isPlaying = viewModel.currentState.value == SpPlayerServiceManager.PlaybackState.Playing,
                color = systemThemeColor(1f),
                modifier = Modifier
                    .size(42.dp)
                    .align(Alignment.CenterVertically)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = { viewModel.skipPrevious() },
            modifier = Modifier
                .clip(CircleShape)
                .size(42.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = oppositeColorOfSystem(0.2f), contentColor = oppositeColorOfSystem(1f))
        ) {
            Icon(imageVector = Icons.Rounded.SkipPrevious, contentDescription = null)
        }

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = { viewModel.skipNext() },
            modifier = Modifier
                .clip(CircleShape)
                .size(42.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = oppositeColorOfSystem(0.2f), contentColor = oppositeColorOfSystem(1f))
        ) {
            Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = null)
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { },
            modifier = Modifier
                .clip(CircleShape)
                .size(42.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = oppositeColorOfSystem(0.2f), contentColor = oppositeColorOfSystem(1f))
        ) {
            Icon(imageVector = Icons.Rounded.FavoriteBorder, contentDescription = null)
        }

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = { setQueueOpened(!queueOpened) },
            modifier = Modifier
                .clip(CircleShape)
                .size(42.dp)
                .onGloballyPositioned { coords ->
                    viewModel.queueButtonParams = coords.positionInRoot()
                },
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent, contentColor = oppositeColorOfSystem(1f))
        ) {
            Icon(imageVector = Icons.Rounded.QueueMusic, contentDescription = null)
        }
    }
}

