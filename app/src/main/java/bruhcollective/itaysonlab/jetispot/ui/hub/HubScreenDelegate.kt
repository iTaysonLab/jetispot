package bruhcollective.itaysonlab.jetispot.ui.hub

import androidx.compose.ui.graphics.Color
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData

interface HubScreenDelegate {
  fun play(data: PlayFromContextData)
  fun isSurroundedWithPadding(): Boolean
  // headers
  suspend fun calculateDominantColor(url: String, dark: Boolean): Color
  suspend fun getLikedSongsCount(artistId: String): Int = 0
}