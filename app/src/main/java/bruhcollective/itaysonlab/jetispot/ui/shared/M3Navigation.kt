package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
  bsState: BottomSheetState,
  isSelected: (String) -> Boolean,
  onSelect: (String) -> Unit,
) {
  val bsDirection = bsState.direction
  val bsProgress = bsState.progress.fraction

  val bsOffset = when {
    bsDirection == 0f -> if (bsState.isCollapsed) 1f - bsProgress else bsProgress
    bsState.isCollapsed && bsDirection == 1f && bsProgress == 1f -> 0f
    bsState.isExpanded && bsDirection == -1f && bsProgress == 1f -> 0f
    else -> if (bsState.direction == 1f) 1f - bsProgress else bsProgress
  }

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination
  val currentDestinationIsFullscreen = allScreens[currentDestination?.route] is FullscreenModeScreen
  if (!currentDestinationIsFullscreen) NavigationBar(modifier = Modifier
    .offset(y = (80.dp + with(LocalDensity.current) { WindowInsets.navigationBars.getBottom(LocalDensity.current).toDp() }) * bsOffset)
    .background(MaterialTheme.colorScheme.compositeSurfaceElevation(3.dp))
    .navigationBarsPadding()) {
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