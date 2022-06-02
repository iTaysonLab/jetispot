package bruhcollective.itaysonlab.jetispot.ui.dac.components_plans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import com.spotify.planoverview.v1.SingleUserPrepaidComponent
import com.spotify.planoverview.v1.SingleUserRecurringComponent
import com.spotify.planoverview.v1.SingleUserTrialComponent

@Composable
@NonRestartableComposable
fun SingleUserComponentBinder(
  item: SingleUserRecurringComponent
) {
  SingleUserComponentImplBinder(
    icon = Icons.Default.Person,
    planName = item.planName,
    planDesc = item.planPrice,
    planText = item.planDescription,
  )
}

@Composable
@NonRestartableComposable
fun SingleUserComponentBinder(
  item: SingleUserPrepaidComponent
) {
  SingleUserComponentImplBinder(
    icon = Icons.Default.Person,
    planName = item.planName,
    planDesc = "prepaid",
    planText = item.planDescription,
  )
}

@Composable
@NonRestartableComposable
fun SingleUserComponentBinder(
  item: SingleUserTrialComponent
) {
  SingleUserComponentImplBinder(
    icon = Icons.Default.Schedule,
    planName = item.planName,
    planDesc = item.planPrice,
    planText = item.planDescription,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleUserComponentImplBinder(
  icon: ImageVector,
  planName: String,
  planDesc: String,
  planText: String
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .fillMaxWidth()
  ) {
    Column {
      Row(Modifier.padding(16.dp)) {
        Surface(
          tonalElevation = 32.dp, modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .size(48.dp)
        ) {
          Icon(
            icon,
            null,
            tint = MaterialTheme.colorScheme.primary,
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
          MediumText(text = planName)
          Subtext(text = planDesc, modifier = Modifier.padding(top = 4.dp))
        }
      }

      Surface(
        tonalElevation = 8.dp, modifier = Modifier
          .height(1.dp)
          .fillMaxWidth()
      ) {}

      Subtext(text = planText, modifier = Modifier.padding(16.dp))
    }
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SingleUserRecurringComponentBinder_Preview() {
  SingleUserComponentBinder(
    SingleUserRecurringComponent.newBuilder().apply {
      planName = "Premium Individual"
      planColor = "#FFD2D7"
      planBillingDate = 1660870274
      planExpirationDate = 1660870274
      planPrice = "17,99 TL"
      planDescription = "Your plan will automatically renew on 8/19/22. You\'ll be charged 17,99 TL/ month."
    }.build()
  )
}

