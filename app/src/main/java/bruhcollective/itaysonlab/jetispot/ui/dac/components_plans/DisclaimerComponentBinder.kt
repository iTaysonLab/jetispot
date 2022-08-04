package bruhcollective.itaysonlab.jetispot.ui.dac.components_plans

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import com.spotify.allplans.v1.DisclaimerComponent

@Composable
fun DisclaimerComponentBinder(
  item: DisclaimerComponent
) {
  Column {
    Subtext(text = item.text, modifier = Modifier.padding(start = 16.dp))
  }
}