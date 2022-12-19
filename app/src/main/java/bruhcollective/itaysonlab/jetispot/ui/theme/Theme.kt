package bruhcollective.itaysonlab.jetispot.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private tailrec fun Context.findWindow(): Window? =
  when (this) {
    is Activity -> window
    is ContextWrapper -> baseContext.findWindow()
    else -> null
  }

@Composable
fun ApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val window = LocalView.current.context.findWindow()
  val view = LocalView.current
  val sysUiController = rememberSystemUiController(window)

  window?.let { WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = darkTheme }

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
