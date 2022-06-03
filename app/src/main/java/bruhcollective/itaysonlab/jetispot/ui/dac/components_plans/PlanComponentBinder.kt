package bruhcollective.itaysonlab.jetispot.ui.dac.components_plans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import com.spotify.allplans.v1.PlanComponent

@Composable
fun PlanComponentBinder(
  navController: LambdaNavigationController,
  item: PlanComponent
) {
  Row(Modifier.clickable {
    navController.openInBrowser(item.uri)
  }.fillMaxWidth().padding(horizontal = 16.dp)) {
    Surface(
      tonalElevation = 32.dp, modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .size(56.dp)
    ) {
      Icon(
        Icons.Default.Paid,
        null,
        tint = Color(android.graphics.Color.parseColor(item.planColor)),
        modifier = Modifier
          .padding(12.dp)
          .fillMaxSize()
      )
    }

    Column(
      Modifier
        .padding(start = 16.dp)
        .align(Alignment.CenterVertically)
    ) {
      MediumText(text = item.planName)
      Subtext(text = item.planPrice, modifier = Modifier.padding(top = 4.dp))
      Subtext(text = item.availableAccounts, modifier = Modifier.padding(top = 4.dp))
    }
  }

  Spacer(modifier = Modifier.height(8.dp))
}