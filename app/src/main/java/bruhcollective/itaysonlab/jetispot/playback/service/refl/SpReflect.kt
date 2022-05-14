package bruhcollective.itaysonlab.jetispot.playback.service.refl

import androidx.media2.common.SessionPlayer
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

  @SessionPlayer.RepeatMode
  fun getRepeatMode(): Int {
    val options = stateOf(player()).options
    return when {
      options.repeatingContext -> SessionPlayer.REPEAT_MODE_ALL
      options.repeatingTrack -> SessionPlayer.REPEAT_MODE_ONE
      else -> SessionPlayer.REPEAT_MODE_NONE
    }
  }

  @SessionPlayer.ShuffleMode
  fun getShuffleMode(): Int {
    return if (stateOf(player()).options.shufflingContext) SessionPlayer.SHUFFLE_MODE_ALL else SessionPlayer.SHUFFLE_MODE_NONE
  }

  private fun stateOf(player: Player) = player.stateWrapper.state
}