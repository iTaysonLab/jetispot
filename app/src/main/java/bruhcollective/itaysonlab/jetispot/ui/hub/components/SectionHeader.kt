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
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate

@Composable
fun SectionHeader(
  text: HubText,
) {
  Box(
    Modifier
      .padding(top = 22.dp, bottom = 4.dp)
      .padding(
        horizontal = if (LocalHubScreenDelegate.current.isSurroundedWithPadding()) 0.dp else 16.dp
      )
  ) {
    Text(
      text = text.title!!,
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Bold,
      fontSize = 24.sp,
      modifier = Modifier.align(Alignment.CenterStart)
    )
  }
}