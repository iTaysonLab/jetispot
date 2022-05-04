package bruhcollective.itaysonlab.jetispot.ui.shared.dynamic_blocks

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DynamicLikeButton(
  objectUrl: String,
  modifier: Modifier = Modifier
) {
  IconButton(onClick = {
    // todo
  }, modifier = modifier) {
    Icon(Icons.Default.Favorite, contentDescription = null)
  }
}