package bruhcollective.itaysonlab.jetispot.ui.dac.components_plans

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import com.spotify.allplans.v1.DisclaimerComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisclaimerComponentBinder(
  navController: NavController,
  item: DisclaimerComponent
) {
  Column {
    Subtext(text = item.text, modifier = Modifier.padding(start = 16.dp))
  }
}