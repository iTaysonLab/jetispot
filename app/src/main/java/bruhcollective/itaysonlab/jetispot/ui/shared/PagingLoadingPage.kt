package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PagingLoadingPage (
  modifier: Modifier
) {
  Box(modifier) {
    CircularProgressIndicator(
      modifier = Modifier
        .align(Alignment.Center)
        .size(56.dp)
    )
  }
}