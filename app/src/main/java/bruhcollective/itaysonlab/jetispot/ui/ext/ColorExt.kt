package bruhcollective.itaysonlab.jetispot.ui.ext

import androidx.annotation.FloatRange
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln

fun ColorScheme.compositeSurfaceElevation(
  elevation: Dp,
): Color {
  if (elevation == 0.dp) return surface
  val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
  return surfaceTint.copy(alpha = alpha).compositeOver(surface)
}

fun Color.blendWith(color: Color, @FloatRange(from = 0.0, to = 1.0) ratio: Float): Color {
  val inv = 1f - ratio
  return copy(
    red = red * inv + color.red * ratio,
    blue = blue * inv + color.blue * ratio,
    green = green * inv + color.green * ratio,
  )
}