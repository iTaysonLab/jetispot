package bruhcollective.itaysonlab.jetispot.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel

@Composable
fun ApplicationTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
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

//@Composable
//private fun providePlayerColorScheme(darkTheme: Boolean): ColorScheme {
//  return when {
//    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) {
//      dynamicDarkColorScheme(LocalContext.current)
//    } else {
//      dynamicLightColorScheme(LocalContext.current)
//    }
//    else -> if (darkTheme) {
//      darkColorScheme()
//    } else {
//      lightColorScheme()
//    }
//  }
//}

@Composable
fun PlayerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
  viewModel: NowPlayingViewModel
) {
//  val currentColor = viewModel.currentBgColor.value
//  val dominantColorAsBg = animateColorAsState(
//    if (currentColor == Color.Transparent) MaterialTheme.colorScheme.surface else currentColor
//  )
  MaterialTheme(
    colorScheme = provideColorScheme(darkTheme),
    shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(8.dp)),
    content = content
  )
}