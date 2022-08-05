package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.dac.DacRender
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import com.spotify.home.dac.component.v1.proto.SnappyGridSectionComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnappyGridSectionComponentBinder(
  item: SnappyGridSectionComponent
) {
  LazyHorizontalGrid(rows = GridCells.Fixed(item.componentsCount),
    Modifier
      .fillMaxWidth()
      .height(56.dp * item.componentsCount)) {
    items(item.componentsList) { cItem ->
      DacRender(item = item, rememberEUCScrollBehavior())
    }
  }
}