package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun PagingErrorPage (
  onReload: () -> Unit,
  modifier: Modifier
) {
  Box(modifier) {
    Column(
      Modifier
        .align(Alignment.Center)
    ) {
      Icon(
        Icons.Default.Error, contentDescription = null, modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .size(56.dp)
          .padding(bottom = 12.dp)
      )
      Text(
        "An error occurred while loading the page.",
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
    }

    OutlinedButton(
      onClick = { onReload() }, modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 16.dp)
    ) {
      Text("Reload")
    }
  }
}