package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spotify.home.dac.component.experimental.v1.proto.FilterComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterComponentBinder (
  scrollBehavior: TopAppBarScrollBehavior,
  component: FilterComponent,
  selectedFacet: String,
  selectFacet: (String) -> Unit
) {
  LazyRow(
    Modifier
      .padding(start = 16.dp, bottom = 16.dp)
      .height(
        animateFloatAsState(
          32 * (1f - scrollBehavior.state.collapsedFraction)
        ).value.dp
      ),
    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    items(component.facetsList) { item ->
      val selected = selectedFacet == item.value
      FilterChip(
        selected = selected,
        onClick = { selectFacet(if (selected) "default" else item.value) },
        label = { Text(item.title) },
        leadingIcon = { if (selected) Icon(Icons.Rounded.Check, null) } )
    }
  }
}