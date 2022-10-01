package bruhcollective.itaysonlab.jetispot.ui.dac

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import com.spotify.dac.player.v1.proto.PlayCommand

@Stable
interface DacDelegate {
  fun dispatchPlay(command: PlayCommand)
}

val LocalDacDelegate = staticCompositionLocalOf<DacDelegate> { error("DacDelegate should be initialized") }