package bruhcollective.itaysonlab.jetispot.ui.hub

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import kotlinx.coroutines.CoroutineScope

interface HubScreenDelegate {
  fun play(data: PlayFromContextData)
  fun isSurroundedWithPadding(): Boolean
  // headers
  suspend fun calculateDominantColor(url: String, dark: Boolean): Color
  suspend fun getLikedSongsCount(artistId: String): Int = 0
  // states
  fun getMainObjectAddedState(): State<Boolean>
  fun sendCustomCommand(scope: CoroutineScope, cmd: Any): Any = Unit
}