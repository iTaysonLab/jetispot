package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Expand
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.SpApp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.lyrics.SpLyricsController
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel

@Composable
fun NowPlayingLyricsComposition(
    modifier: Modifier,
    viewModel: NowPlayingViewModel,
    rvStateProgress: Float
) {
    Box(modifier) {
        val color = oppositeColorOfSystem(alpha = 0.2f)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val offset = Offset(
                x = lerp(viewModel.lyricsCardParams.first.x, 0f, rvStateProgress),
                y = lerp(viewModel.lyricsCardParams.first.y, 0f, rvStateProgress),
            )

            val size = Size(
                width = lerp(
                    viewModel.lyricsCardParams.second.width.toFloat(),
                    size.width,
                    rvStateProgress
                ),
                height = lerp(
                    viewModel.lyricsCardParams.second.height.toFloat(),
                    size.height,
                    rvStateProgress
                ),
            )

            val radius = androidx.compose.ui.unit.lerp(12.dp, 0.dp, rvStateProgress).toPx()

            drawRoundRect(
                color = color,
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

                LazyColumn(modifier = Modifier.padding(12.dp)) {
                    if (viewModel.spLyricsController.currentLyricsState == SpLyricsController.LyricsState.Unavailable) {
                        items(listOf(SpApp.context.getString(R.string.no_lyrics))) { item ->
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = item,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                        }
                    } else {
                        items(viewModel.spLyricsController.currentLyricsLines) { line ->
                            Text(
                                text = line.words,
                                color = oppositeColorOfSystem(alpha = 1f),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun lerp(a: Float, b: Float, to: Float): Float {
    return a + to * (b - a)
}