package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubText

@Composable
fun TextRow(
  text: HubText
) {
  Text(
    text.title ?: text.description ?: "",
    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (text.description == null) 1f else 0.7f),
    fontSize = 14.sp,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
  )
}