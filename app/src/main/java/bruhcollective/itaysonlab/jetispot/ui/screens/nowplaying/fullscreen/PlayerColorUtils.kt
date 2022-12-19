package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.fullscreen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//return color black if system theme is light and white if system theme is dark
@Composable
fun oppositeColorOfSystem(alpha : Float): Color {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    return if (isSystemInDarkTheme) Color.White.copy(alpha = alpha) else Color.Black.copy(alpha = alpha)
}

//return the color of the system theme
@Composable
fun systemThemeColor(alpha : Float): Color {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    return if (isSystemInDarkTheme) Color.Black.copy(alpha = alpha) else Color.White.copy(alpha = alpha)
}