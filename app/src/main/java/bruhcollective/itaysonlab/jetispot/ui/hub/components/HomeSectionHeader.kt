package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubText

@Composable
fun HomeSectionHeader (
  text: HubText
) {
  Box(Modifier.padding(vertical = 8.dp)) {
    Text(text = text.title!!, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 21.sp, modifier = Modifier.align(Alignment.CenterStart))
  }
}