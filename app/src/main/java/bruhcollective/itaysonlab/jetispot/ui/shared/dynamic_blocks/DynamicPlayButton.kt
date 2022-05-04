package bruhcollective.itaysonlab.jetispot.ui.shared.dynamic_blocks

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.spotify.dac.player.v1.proto.PlayCommand

@Composable
fun DynamicPlayButton(
  command: PlayCommand,
  modifier: Modifier = Modifier
) {
  IconButton(onClick = {
    // todo
  }, modifier = modifier.clip(CircleShape)) {
    Icon(Icons.Default.PlayArrow, contentDescription = null)
  }
}