package bruhcollective.itaysonlab.jetispot.ui.hub

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import bruhcollective.itaysonlab.jetispot.core.objs.hub.PlayFromContextData

interface HubScreenDelegate {
  fun play(data: PlayFromContextData)
  fun isSurroundedWithPadding(): Boolean
  // headers
  suspend fun calculateDominantColor(drawable: Drawable): Color
}