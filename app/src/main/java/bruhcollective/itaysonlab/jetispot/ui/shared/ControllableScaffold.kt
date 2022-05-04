package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// A slimmed down version of Scaffold for usage with TopAppBar + Content, but with "draw content under top app bar" option
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllableScaffold(
  modifier: Modifier = Modifier,
  drawContentUnderTopBar: Boolean,
  topBar: @Composable () -> Unit = {},
  content: @Composable (PaddingValues) -> Unit,
) {
  if (drawContentUnderTopBar) {
    Surface(modifier = modifier) {
      content(PaddingValues(0.dp))
      topBar()
    }
  } else {
    Scaffold(topBar = topBar, modifier = modifier, content = content)
  }
}