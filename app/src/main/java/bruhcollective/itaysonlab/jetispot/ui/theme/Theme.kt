package bruhcollective.itaysonlab.jetispot.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.monet.color
import bruhcollective.itaysonlab.jetispot.ui.monet.google.scheme.Scheme
import bruhcollective.itaysonlab.jetispot.ui.shared.AppPreferences
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val sysUiController = rememberSystemUiController()

  SideEffect {
    sysUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = !darkTheme)
  }

  MaterialTheme(
    colorScheme = provideColorScheme(darkTheme),
    shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(8.dp)),
    content = content
  )
}

@Composable
private fun provideColorScheme(darkTheme: Boolean): ColorScheme {
  val ColorOfScheme = AppPreferences.ColorScheme
  val DScheme = Scheme.dark(android.graphics.Color.parseColor(ColorOfScheme))
  val DarkColorScheme : ColorScheme = ColorScheme(
    primary = DScheme.primary.color(),
    onPrimary = DScheme.onPrimary.color(),
    primaryContainer = DScheme.primaryContainer.color(),
    onPrimaryContainer = DScheme.onPrimaryContainer.color(),
    inversePrimary = DScheme.inversePrimary.color(),
    secondary = DScheme.secondary.color(),
    onSecondary = DScheme.onSecondary.color(),
    secondaryContainer = DScheme.secondaryContainer.color(),
    onSecondaryContainer = DScheme.onSecondaryContainer.color(),
    tertiary = DScheme.tertiary.color(),
    onTertiary = DScheme.onTertiary.color(),
    tertiaryContainer = DScheme.tertiaryContainer.color(),
    onTertiaryContainer = DScheme.onTertiaryContainer.color(),
    background = DScheme.background.color(),
    onBackground = DScheme.onBackground.color(),
    surface = DScheme.surface.color(),
    onSurface = DScheme.onSurface.color(),
    surfaceVariant = DScheme.surfaceVariant.color(),
    onSurfaceVariant = DScheme.onSurfaceVariant.color(),
    surfaceTint = DScheme.primary.color(),
    inverseSurface = DScheme.inverseSurface.color(),
    inverseOnSurface = DScheme.inverseOnSurface.color(),
    error = DScheme.error.color(),
    onError = DScheme.onError.color(),
    errorContainer = DScheme.errorContainer.color(),
    onErrorContainer = DScheme.onErrorContainer.color(),
    outline = DScheme.outline.color(),
    outlineVariant = DScheme.outlineVariant.color(),
    scrim = DScheme.scrim.color()
  )

  val LScheme = Scheme.light(android.graphics.Color.parseColor(ColorOfScheme))
  val LightColorScheme = ColorScheme(
    primary = LScheme.primary.color(),
    onPrimary = LScheme.onPrimary.color(),
    primaryContainer = LScheme.primaryContainer.color(),
    onPrimaryContainer = LScheme.onPrimaryContainer.color(),
    inversePrimary = LScheme.inversePrimary.color(),
    secondary = LScheme.secondary.color(),
    onSecondary = LScheme.onSecondary.color(),
    secondaryContainer = LScheme.secondaryContainer.color(),
    onSecondaryContainer = LScheme.onSecondaryContainer.color(),
    tertiary = LScheme.tertiary.color(),
    onTertiary = LScheme.onTertiary.color(),
    tertiaryContainer = LScheme.tertiaryContainer.color(),
    onTertiaryContainer = LScheme.onTertiaryContainer.color(),
    background = LScheme.background.color(),
    onBackground = LScheme.onBackground.color(),
    surface = LScheme.surface.color(),
    onSurface = LScheme.onSurface.color(),
    surfaceVariant = LScheme.surfaceVariant.color(),
    onSurfaceVariant = LScheme.onSurfaceVariant.color(),
    surfaceTint = LScheme.primary.color(),
    inverseSurface = LScheme.inverseSurface.color(),
    inverseOnSurface = LScheme.inverseOnSurface.color(),
    error = LScheme.error.color(),
    onError = LScheme.onError.color(),
    errorContainer = LScheme.errorContainer.color(),
    onErrorContainer = LScheme.onErrorContainer.color(),
    outline = LScheme.outline.color(),
    outlineVariant = DScheme.outlineVariant.color(),
    scrim = DScheme.scrim.color()
  )
  return when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) {
      dynamicDarkColorScheme(LocalContext.current)
    } else {
      dynamicLightColorScheme(LocalContext.current)
    }
    else ->
      if (darkTheme) {
        DarkColorScheme
      } else {
        LightColorScheme
      }
    /*
    if (darkTheme) {
      darkColorScheme()
    } else {
      lightColorScheme()
    }
    */
  }
}
