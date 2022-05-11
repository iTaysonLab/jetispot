package bruhcollective.itaysonlab.jetispot.ui.dac.components_plans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import coil.compose.AsyncImage
import com.spotify.planoverview.v1.MultiUserMemberComponent
import com.spotify.planoverview.v1.UserType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiUserMemberComponentBinder(
  navController: NavController,
  item: MultiUserMemberComponent
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
            Icons.Default.Group,
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
          MediumText(text = item.planName)
          Subtext(text = item.planDescription, modifier = Modifier.padding(top = 4.dp))
        }
      }

      item.planMembersList.forEach { member ->
        Surface(
          tonalElevation = 8.dp, modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
        ) {}

        Row(
          Modifier
            .clickable {

            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 12.dp)
        ) {
          Box(Modifier.size(42.dp)
            .clip(CircleShape)) {
            Surface(tonalElevation = 32.dp, modifier = Modifier.fillMaxSize()) {}
            AsyncImage(model = member.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
          }

          Column(
            Modifier
              .padding(start = 16.dp)
              .align(Alignment.CenterVertically)
              .weight(1f)
          ) {
            MediumText(text = member.name)
            Subtext(text = stringResource(id = when (member.userType) {
              UserType.MEMBER -> R.string.plan_member
              UserType.MANAGER -> R.string.plan_manager
              UserType.KID -> R.string.plan_kid
              else -> R.string.unknown
            }), modifier = Modifier.padding(top = 4.dp))
          }

          if (member.isCurrentUser) {
            Icon(
              Icons.Default.Check,
              null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterVertically)
            )
          }
        }
      }
    }
  }
}