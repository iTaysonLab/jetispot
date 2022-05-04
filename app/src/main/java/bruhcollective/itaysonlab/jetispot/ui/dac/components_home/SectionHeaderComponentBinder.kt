package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionHeaderComponentBinder (
  text: String
) {
  Text(text = text, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 21.sp, modifier = Modifier.padding(16.dp))
}