package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import com.spotify.home.dac.component.v1.proto.RecsplanationHeadingComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecsplanationHeadingComponentBinder(
  item: RecsplanationHeadingComponent
) {
  val navController = LocalNavigationController.current

  Card(
    shape = RoundedCornerShape(32.dp),
    modifier = Modifier.padding(start = 12.dp, top = 40.dp, bottom = 16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer
    )
  ) {
    Row(
      Modifier
        .clickable { navController.navigate(item.navigateUri) }
        .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
      PreviewableAsyncImage(
        imageUrl = item.imageUri,
        placeholderType = "none",
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
      )

      Column(
        Modifier
          .padding(horizontal = 12.dp)
          .align(Alignment.CenterVertically)
      ) {
        Text(item.subtitle.uppercase(), fontSize = 12.sp)
        MediumText(item.title, modifier = Modifier.padding(top = 0.dp), fontSize = 18.sp)
      }
    }
  }

}