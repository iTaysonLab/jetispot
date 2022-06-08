package bruhcollective.itaysonlab.jetispot.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
  return when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) {
      dynamicDarkColorScheme(LocalContext.current)
    } else {
      dynamicLightColorScheme(LocalContext.current)
    }
    else -> if (darkTheme) {
      darkColorScheme()
    } else {
      lightColorScheme()
    }
  }
}
