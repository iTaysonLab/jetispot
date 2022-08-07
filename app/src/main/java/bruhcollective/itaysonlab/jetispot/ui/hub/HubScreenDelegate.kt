package bruhcollective.itaysonlab.jetispot.ui.hub

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import kotlinx.coroutines.CoroutineScope

@Stable
interface HubScreenDelegate {
  fun play(data: PlayFromContextData)
  fun isSurroundedWithPadding(): Boolean = false
  // headers
  suspend fun calculateDominantColor(url: String, dark: Boolean): Color = Color.Transparent
  suspend fun getLikedSongsCount(artistId: String): Int = 0
  // states
  fun getMainObjectAddedState(): State<Boolean> = mutableStateOf(false)
  fun toggleMainObjectAddedState() {}
  fun sendCustomCommand(scope: CoroutineScope, cmd: Any): Any = Unit
}

val LocalHubScreenDelegate = staticCompositionLocalOf<HubScreenDelegate> { error("HubScreenDelegate should be initialized") }