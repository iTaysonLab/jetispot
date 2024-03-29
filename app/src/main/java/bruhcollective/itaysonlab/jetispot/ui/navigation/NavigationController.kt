package bruhcollective.itaysonlab.jetispot.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import bruhcollective.itaysonlab.jetispot.ui.screens.BottomSheet
import bruhcollective.itaysonlab.jetispot.ui.screens.Dialog
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen

@JvmInline
@Immutable
value class NavigationController(
  val controller: () -> NavHostController
) {
  // Not recommended
  fun navigate(route: String) = controller().navigate(route)

  fun navigate(screen: Screen) = controller().navigate(screen.route)
  fun navigate(dialog: Dialog) = controller().navigate(dialog.route)

  fun navigate(sheet: BottomSheet, args: Map<String, String>) {
    var url = sheet.route

    args.forEach { entry ->
      url = url.replace("{${entry.key}}", entry.value)
    }

    controller().navigate(url)
  }

  fun navigateAndClearStack(screen: Screen) = controller().navigate(screen.route) { popUpTo(Screen.NavGraph.route) }

  fun popBackStack() = controller().popBackStack()

  fun context() = controller().context
  fun string(@StringRes id: Int) = context().getString(id)
  fun openInBrowser(uri: String) = context().startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri)))
}

val LocalNavigationController = staticCompositionLocalOf<NavigationController> { error("Supply NavigationController in composable scope!") }