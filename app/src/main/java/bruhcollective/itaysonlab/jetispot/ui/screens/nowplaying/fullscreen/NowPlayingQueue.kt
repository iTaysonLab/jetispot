package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import bruhcollective.itaysonlab.jetispot.core.ext.imageUrl
import bruhcollective.itaysonlab.jetispot.ui.ext.blendWith
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext

@Composable
fun NowPlayingQueue(
    modifier: Modifier,
    viewModel: NowPlayingViewModel,
    rvStateProgress: Float
) {
    val backgroundColor = if (isSystemInDarkTheme())
        MaterialTheme.colorScheme.surface.blendWith(MaterialTheme.colorScheme.primary, ratio = 0.05f)
    else
        MaterialTheme.colorScheme.surface.blendWith(MaterialTheme.colorScheme.primary, ratio = 0.1f)
    Box(modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val offsetPx = 4.dp.toPx()
            val sizePx = 0.dp.toPx()

            val offset = Offset(
                x = lerp(0f, 0f, rvStateProgress),
                y = lerp(0f, 0f, rvStateProgress),
            )

            val size = Size(
                width = lerp(sizePx, size.width, rvStateProgress),
                height = lerp(sizePx, size.height, rvStateProgress),
            )

            val radius = lerp(36.dp, 0.dp, rvStateProgress).toPx()

            drawRoundRect(
                color = backgroundColor,
                topLeft = offset,
                size = size,
                cornerRadius = CornerRadius(radius, radius)
            )
        }

        if (rvStateProgress != 0f) {
            Box(
                Modifier
                    .alpha(rvStateProgress)
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = 56.dp)
                    .offset {
                        IntOffset(x = 0, y = (48.dp.toPx() * (1f - rvStateProgress)).toInt())
                    }) {

                LazyColumn(contentPadding = PaddingValues(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())) {
                    items(viewModel.currentQueue.value) { queueItem ->
                        QueueItem(queueItem)
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueItem(
    item: com.spotify.metadata.Metadata.Track
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp)
    ) {
        PreviewableAsyncImage(
            imageUrl = remember(item) { item.imageUrl },
            placeholderType = "track",
            modifier = Modifier
                .align(
                    Alignment.CenterVertically
                )
                .size(48.dp)
        )

        Column(
            Modifier
                .padding(
                    start = 16.dp
                )
                .align(Alignment.CenterVertically)
        ) {
            MediumText(item.name, fontWeight = FontWeight.Normal)
            Spacer(modifier = Modifier.height(4.dp))
            Subtext(item.artistList.joinToString(", ") { it.name }, maxLines = 1,)
        }
    }
}

private fun lerp(a: Float, b: Float, to: Float): Float {
    return a + to * (b - a)
}