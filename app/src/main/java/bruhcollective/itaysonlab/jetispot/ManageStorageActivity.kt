package bruhcollective.itaysonlab.jetispot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.StorageScreen
import bruhcollective.itaysonlab.jetispot.ui.theme.ApplicationTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageStorageActivity: ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      val navController = rememberNavController()
      val lambdaNavController = LambdaNavigationController { navController }
      val sysUiController = rememberSystemUiController()
      val isDark = isSystemInDarkTheme()

      SideEffect {
        sysUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = !isDark)
      }

      ApplicationTheme {
        NavHost(navController = navController, startDestination = "config/storage") {
          composable(Screen.StorageConfig.route) {
            StorageScreen(lambdaNavController)
          }
        }
      }
    }
  }
}