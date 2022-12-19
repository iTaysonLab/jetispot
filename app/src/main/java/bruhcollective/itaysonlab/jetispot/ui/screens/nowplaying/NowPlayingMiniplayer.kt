package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.ui.shared.MarqueeText
import bruhcollective.itaysonlab.jetispot.ui.shared.PlayPauseButton
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableSyncImage

@Composable
fun NowPlayingMiniplayer2(
    viewModel: NowPlayingViewModel,
    modifier: Modifier,
    visible: Boolean,
    bsOffset: Float,
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Surface(tonalElevation = 8.dp, modifier = modifier) {
            Box(Modifier.fillMaxSize()) {
                Row(
                    Modifier
                        .fillMaxHeight(0.98f)
                        .padding(horizontal = 16.dp)
                ) {
                    PreviewableSyncImage(
                        viewModel.currentTrack.value.artworkCompose,
                        placeholderType = "track",
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterVertically)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                    ) {
                        MarqueeText(
                            if (viewModel.currentTrack.value.title == "Unknown Title") stringResource(
                                id = R.string.unknown_title
                            ) else viewModel.currentTrack.value.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        MarqueeText(
                            if (viewModel.currentTrack.value.artist == "Unknown Artist") stringResource(
                                id = R.string.unknown_artist
                            ) else viewModel.currentTrack.value.artist,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                        )
                    }

                }
                LinearProgressIndicator(
                    progress = viewModel.currentPosition.value.progressRange,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

    @Composable
    fun NowPlayingMiniplayer(
        viewModel: NowPlayingViewModel,
        modifier: Modifier,
        visible: Boolean,
        bsOffset: Float,
    ) {
        AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
            Surface(tonalElevation = 8.dp, modifier = modifier) {
                Box(Modifier.fillMaxSize()) {
                    LinearProgressIndicator(
                        progress = viewModel.currentPosition.value.progressRange,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    )
                    Row(
                        Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 16.dp)
                    ) {
                        PreviewableSyncImage(
                            viewModel.currentTrack.value.artworkCompose,
                            placeholderType = "track",
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Column(
                            Modifier
                                .weight(2f)
                                .padding(horizontal = 14.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            MarqueeText(
                                if (viewModel.currentTrack.value.title == "Unknown Title") stringResource(
                                    id = R.string.unknown_title
                                ) else viewModel.currentTrack.value.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            MarqueeText(
                                if (viewModel.currentTrack.value.artist == "Unknown Artist") stringResource(
                                    id = R.string.unknown_artist
                                ) else viewModel.currentTrack.value.artist,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .width(64.dp)
                                .fillMaxHeight()
                                .padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.1f),
                            shape = CircleShape
                        ) {
                            PlayPauseButton(
                                viewModel.currentState.value == SpPlayerServiceManager.PlaybackState.Playing,
                                MaterialTheme.colorScheme.onSecondaryContainer.copy(0.85f),
                                Modifier
                                    .width(56.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable { viewModel.togglePlayPause() }
                            )
                        }
                    }
                }
            }
        }
    }

/*
                    Row(
                        Modifier
                            .fillMaxHeight(0.98f)
                            .padding(horizontal = 16.dp)
                    ) {
                        PreviewableSyncImage(
                            viewModel.currentTrack.value.artworkCompose,
                            placeholderType = "track",
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                        ) {
                            MarqueeText(
                                if (viewModel.currentTrack.value.title == "Unknown Title") stringResource(
                                    id = R.string.unknown_title
                                ) else viewModel.currentTrack.value.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            MarqueeText(
                                if (viewModel.currentTrack.value.artist == "Unknown Artist") stringResource(
                                    id = R.string.unknown_artist
                                ) else viewModel.currentTrack.value.artist,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                            )
                        }
                        Row(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 16.dp)
                                    .size(32.dp),
                            ) {
                                PlayPauseButton(
                                    viewModel.currentState.value == SpPlayerServiceManager.PlaybackState.Playing,
                                    MaterialTheme.colorScheme.onSecondaryContainer.copy(0.85f),
                                    Modifier
                                        .width(56.dp)
                                        .align(Alignment.CenterVertically)
                                        .clickable { viewModel.togglePlayPause() }
                                )
                            }
                        }
                    }
 */

// ------------------------------------------------------------------------------------

/* ORIGINAL
@Composable
fun NowPlayingMiniplayer(
    viewModel: NowPlayingViewModel,
    modifier: Modifier,
    visible: Boolean
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Surface(tonalElevation = 8.dp, modifier = modifier) {
            Box(Modifier.fillMaxSize()) {
                LinearProgressIndicator(
                    progress = viewModel.currentPosition.value.progressRange,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                      .height(2.dp)
                      .fillMaxWidth()
                )

                Row(
                  Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp)
                ) {
                    PreviewableSyncImage(
                        viewModel.currentTrack.value.artworkCompose,
                        placeholderType = "track",
                        modifier = Modifier
                          .size(48.dp)
                          .align(Alignment.CenterVertically)
                          .clip(RoundedCornerShape(8.dp))
                    )

                    Column(
                      Modifier
                        .weight(2f)
                        .padding(horizontal = 14.dp)
                        .align(Alignment.CenterVertically)
                    ) {
                        Text(
                          if (viewModel.currentTrack.value.title == "Unknown Title") stringResource(id = R.string.unknown_title) else viewModel.currentTrack.value.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp
                        )
                        Text(
                            if(viewModel.currentTrack.value.artist == "Unknown Artist") stringResource(id = R.string.unknown_artist) else viewModel.currentTrack.value.artist,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    PlayPauseButton(
                        viewModel.currentState.value == SpPlayerServiceManager.PlaybackState.Playing,
                        MaterialTheme.colorScheme.onSurface,
                      Modifier
                        .fillMaxHeight()
                        .width(56.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                          viewModel.togglePlayPause()
                        }
                    )
                }
            }
        }
    }
}
*/