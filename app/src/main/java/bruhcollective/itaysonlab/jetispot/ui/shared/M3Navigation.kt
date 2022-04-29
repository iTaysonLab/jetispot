package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.screens.FullscreenModeScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.allScreens
import bruhcollective.itaysonlab.jetispot.ui.screens.bottomNavigationScreens

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun M3Navigation(
  navController: NavController,
  bsOffset: Float,
  isSelected: (String) -> Boolean,
  onSelect: (String) -> Unit,
) {
  val navBarHeight = with(LocalDensity.current) { WindowInsets.navigationBars.getBottom(LocalDensity.current).toDp() }
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination
  val currentDestinationIsFullscreen = allScreens[currentDestination?.route] is FullscreenModeScreen
  if (!currentDestinationIsFullscreen) bruhcollective.itaysonlab.jetispot.ui.shared.evo.NavigationBar(modifier = Modifier
    .offset(y = (80.dp + navBarHeight) * bsOffset)
    .background(MaterialTheme.colorScheme.compositeSurfaceElevation(3.dp)),
  contentPadding = PaddingValues(bottom = navBarHeight)) {
    bottomNavigationScreens.forEach { screen ->
      NavigationBarItem(
        icon = { Icon(screen.iconProvider(), contentDescription = stringResource(screen.name)) },
        label = { Text(stringResource(screen.name)) },
        selected = isSelected(screen.route),
        onClick = {
          onSelect(screen.route)
          navController.navigate(screen.route) {
            popUpTo(navController.graph.findStartDestination().id) {
              saveState = true
            }

            launchSingleTop = true
            restoreState = true
          }
        }
      )
    }
  }
}