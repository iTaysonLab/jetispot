package bruhcollective.itaysonlab.jetispot.playback.service.refl

import xyz.gianlu.librespot.player.Player
import xyz.gianlu.librespot.player.StateWrapper

class SpReflect(
  private val player: () -> Player
) {
  private val getStateWrapperFromPlayer = Player::class.java.getDeclaredField("state").also { it.isAccessible = true }
  private val getStateFromStateWrapper = StateWrapper::class.java.getDeclaredField("state").also { it.isAccessible = true }

  @androidx.media3.common.Player.RepeatMode
  fun getRepeatMode(): Int {
    val options = stateOf(player()).options
    return when {
      options.repeatingContext -> androidx.media3.common.Player.REPEAT_MODE_ALL
      options.repeatingTrack -> androidx.media3.common.Player.REPEAT_MODE_ONE
      else -> androidx.media3.common.Player.REPEAT_MODE_OFF
    }
  }

  fun isShuffleModeEnabled(): Boolean {
    return stateOf(player()).options.shufflingContext
  }

  private fun stateOf(player: Player) = getStateFromStateWrapper.get(getStateWrapperFromPlayer.get(player)) as com.spotify.connectstate.Player.PlayerState.Builder
}