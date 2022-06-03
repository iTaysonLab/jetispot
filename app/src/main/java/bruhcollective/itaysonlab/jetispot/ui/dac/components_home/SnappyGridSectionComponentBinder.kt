package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.dac.DacRender
import com.spotify.home.dac.component.v1.proto.SnappyGridSectionComponent

@Composable
fun SnappyGridSectionComponentBinder(
  navController: LambdaNavigationController,
  item: SnappyGridSectionComponent
) {
  LazyHorizontalGrid(rows = GridCells.Fixed(item.componentsCount), Modifier.fillMaxWidth().height(56.dp * item.componentsCount)) {
    items(item.componentsList) { cItem ->
      DacRender(navController = navController, item = item)
    }
  }
}