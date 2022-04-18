package bruhcollective.itaysonlab.jetispot.playback.service.refl

import androidx.media2.common.SessionPlayer
import xyz.gianlu.librespot.player.Player
import xyz.gianlu.librespot.player.StateWrapper

class SpReflect(
  private val player: () -> Player
) {
  private val getStateWrapperFromPlayer = Player::class.java.getDeclaredField("state").also { it.isAccessible = true }
  private val getStateFromStateWrapper = StateWrapper::class.java.getDeclaredField("state").also { it.isAccessible = true }

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

  private fun stateOf(player: Player) = getStateFromStateWrapper.get(getStateWrapperFromPlayer.get(player)) as com.spotify.connectstate.Player.PlayerState.Builder
}