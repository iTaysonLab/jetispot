package bruhcollective.itaysonlab.jetispot.ui.dac.components_plans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import com.spotify.planoverview.v1.BenefitListComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenefitListComponentBinder(
  navController: NavController,
  item: BenefitListComponent
) {
  Card(
    containerColor = MaterialTheme.colorScheme.surface,
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .fillMaxWidth()
  ) {
    Column {
      Row(Modifier.padding(16.dp)) {
        MediumText(text = stringResource(id = R.string.plan_includes))
      }

      Surface(
        tonalElevation = 8.dp, modifier = Modifier
          .height(1.dp)
          .fillMaxWidth()
      ) {}

      item.benefitsList.forEach { benefit ->
        Row(
          Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 12.dp)
        ) {
          Icon(
            Icons.Default.Check,
            null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
              .size(24.dp)
              .align(Alignment.CenterVertically)
          )

          Text(text = benefit.text, fontSize = 12.sp, modifier = Modifier
            .padding(start = 16.dp)
            .align(Alignment.CenterVertically))
        }
      }

      if (item.showButton) {
        Surface(
          tonalElevation = 8.dp, modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
        ) {}

        Row(
          Modifier
            .clickable {
              navController.navigate("dac/viewAllPlans")
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 12.dp)
        ) {
          Text(text = stringResource(id = R.string.plan_others), fontSize = 14.sp, modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically))

          Icon(
            Icons.Default.ChevronRight,
            null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
              .size(24.dp)
              .align(Alignment.CenterVertically)
          )
        }
      }
    }
  }
}