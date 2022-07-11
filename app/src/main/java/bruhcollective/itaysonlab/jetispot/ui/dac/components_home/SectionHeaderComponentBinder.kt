package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionHeaderComponentBinder (
  text: String
) {
  Text(
    text = text,
    color = MaterialTheme.colorScheme.onSurface,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(top = 32.dp, bottom = 12.dp)
  )
}