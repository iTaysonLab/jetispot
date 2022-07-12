package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.ui.shared.PlayPauseButton
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableSyncImage

@Composable
fun NowPlayingMiniplayer(
  viewModel: NowPlayingViewModel,
  modifier: Modifier,
  bsOffset: Float
) {
  Surface(color = Color.Transparent, modifier = modifier) {
    Box(
      Modifier
        .fillMaxSize()
    ) {
      LinearProgressIndicator(
        progress = viewModel.currentPosition.value.progressRange,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .height(2.dp)
          .fillMaxWidth()
      )

      Row(
        Modifier
          .padding(horizontal = 16.dp)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(Modifier.padding(top = 8.dp)) {
          Surface(
            modifier = Modifier
              .size(48.dp + (bsOffset * 1000).dp)
//              .align(Alignment.Top)
              .absoluteOffset(
                x = with(LocalDensity.current) { (1f + (bsOffset * 10f)).toDp() },
                y = with(LocalDensity.current) {
                  (LocalConfiguration.current.screenWidthDp * 0.9 * bsOffset).dp
                }
              )
              .clip(RoundedCornerShape((8 + (bsOffset * 64)).dp))
          ) {
            PreviewableSyncImage(
              viewModel.currentTrack.value.artworkCompose,
              placeholderType = "track",
              modifier = Modifier
                .fillMaxSize()
            )
          }


          Column(
            Modifier
              .padding(horizontal = 12.dp)
              .align(Alignment.CenterVertically)
          ) {
            Text(
              viewModel.currentTrack.value.title,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              fontSize = 16.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              viewModel.currentTrack.value.artist,
              color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }

        Surface(
          shape = CircleShape,
          modifier = Modifier
            .height(64.dp)
            .padding(vertical = 12.dp),
          color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.1f)
        ) {
          PlayPauseButton(
            viewModel.currentState.value == SpPlayerServiceManager.PlaybackState.Playing,
            MaterialTheme.colorScheme.onSecondaryContainer.copy(0.85f),
            Modifier
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