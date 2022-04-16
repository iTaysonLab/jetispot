package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun MediumText (
  text: String,
  modifier: Modifier = Modifier,
  color: Color = Color.Unspecified,
  fontWeight: FontWeight = FontWeight.Bold,
  fontSize: TextUnit = 16.sp,
) {
  Text(text, color = color, fontSize = fontSize, fontWeight = fontWeight, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = modifier)
}

@Composable
fun Subtext (
  text: String,
  modifier: Modifier = Modifier
) {
  Text(text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 12.sp, lineHeight = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = modifier)
}

@Composable
fun SubtextOverline (
  text: String,
  modifier: Modifier = Modifier
) {
  Text(text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), letterSpacing = 2.sp, fontSize = 12.sp, lineHeight = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = modifier)
}