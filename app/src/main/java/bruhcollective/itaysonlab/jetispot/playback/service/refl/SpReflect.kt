package bruhcollective.itaysonlab.jetispot.playback.service.refl

import com.google.gson.JsonParser
import xyz.gianlu.librespot.player.Player

class SpReflect(
  private val player: () -> Player
) {
  fun playUsingData (data: String) {
    try {
      player().callPlayFromObj(JsonParser.parseString(data).asJsonObject)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  @androidx.media3.common.Player.RepeatMode
  fun getRepeatMode(): Int {
    val options = stateOf(player()).options
    return when {
      options.repeatingContext -> androidx.media3.common.Player.REPEAT_MODE_ALL
      options.repeatingTrack -> androidx.media3.common.Player.REPEAT_MODE_ONE
      else -> androidx.media3.common.Player.REPEAT_MODE_OFF
    }
  }

  fun getShuffleMode(): Boolean {
    return stateOf(player()).options.shufflingContext
  }

  private fun stateOf(player: Player) = player.stateWrapper.state
}