package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.round
import kotlin.math.roundToInt

class PlayPauseButtonState (isPlaying: Boolean) {
  val isFinallyPlay = mutableStateOf(isPlaying)
  val leftBar = Path()
  val rightBar = Path()
}

class PlayPauseButtonDimens(
  val distance: Dp = 3.dp,
  val width: Dp = 6.dp,
  val height: Dp = 16.dp
)

@Composable
fun PlayPauseButton (
  isPlaying: Boolean,
  color: Color,
  modifier: Modifier
) {
  val state = remember { PlayPauseButtonState(isPlaying) }
  val dimens = remember { PlayPauseButtonDimens() }

  val progressAnimator = animateFloatAsState(
    targetValue = if (!isPlaying) 1f else 0f,
    finishedListener = { state.isFinallyPlay.value = it == 1f }
  )

  Canvas(modifier) {
    val progress = progressAnimator.value
    val pauseBarDistance = dimens.distance.toPx()
    val pauseBarWidth = dimens.width.toPx()
    val pauseBarHeight = dimens.height.toPx()

    val barDist = lerp(pauseBarDistance, 0f, progress)
    val rawBarWidth = lerp(pauseBarWidth, pauseBarHeight / 1.75f, progress)
    val barWidth = (if (progress == 1f) rawBarWidth.roundToInt() else rawBarWidth).toFloat()

    val firstBarTopLeft = lerp(0f, barWidth, progress)
    val secondBarTopRight = lerp(2f * barWidth + barDist, barWidth + barDist, progress)

    state.leftBar.apply {
      reset()
      moveTo(0f, 0f)
      lineTo(firstBarTopLeft, -pauseBarHeight)
      lineTo(barWidth, -pauseBarHeight)
      lineTo(barWidth, 0f)
      close()
    }

    state.rightBar.apply {
      reset()
      moveTo(barWidth + barDist, 0f)
      lineTo(barWidth + barDist, -pauseBarHeight)
      lineTo(secondBarTopRight, -pauseBarHeight)
      lineTo(2 * barWidth + barDist, 0f)
      close()
    }

    translate(lerp(0f, pauseBarHeight / 8f, progress), 0f) {
      val rotationProgress = if (state.isFinallyPlay.value) 1f - progress else progress
      val startingRotation = if (state.isFinallyPlay.value) 90f else 0f
      rotate(lerp(startingRotation, startingRotation + 90f, rotationProgress), Offset(size.width / 2f, size.height / 2f)) {
        translate(round(size.width / 2f - ((2f * barWidth + barDist) / 2f)), round(size.height / 2f + (pauseBarHeight / 2f))) {
          drawPath(state.leftBar, color)
          drawPath(state.rightBar, color)
        }
      }
    }
  }
}