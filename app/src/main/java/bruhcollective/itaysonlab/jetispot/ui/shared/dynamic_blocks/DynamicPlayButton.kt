package bruhcollective.itaysonlab.jetispot.ui.shared.dynamic_blocks

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import bruhcollective.itaysonlab.jetispot.ui.dac.LocalDacDelegate
import com.spotify.dac.player.v1.proto.PlayCommand

@Composable
fun DynamicPlayButton(
    command: PlayCommand,
    modifier: Modifier = Modifier
) {
    val dacDelegate = LocalDacDelegate.current
    FilledIconButton(
        onClick = { dacDelegate.dispatchPlay(command) }, modifier = modifier.clip(CircleShape)
    ) {
        Icon(Icons.Rounded.PlayArrow, contentDescription = null)
    }
}