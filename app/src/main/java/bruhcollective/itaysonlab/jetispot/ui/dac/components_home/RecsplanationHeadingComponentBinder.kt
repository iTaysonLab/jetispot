package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.SubtextOverline
import com.spotify.home.dac.component.v1.proto.RecsplanationHeadingComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecsplanationHeadingComponentBinder(
  navController: LambdaNavigationController,
  item: RecsplanationHeadingComponent
) {
  Card(shape = RoundedCornerShape(32.dp), modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 16.dp)) {
    Row(
      Modifier
        .clickable { navController.navigate(item.navigateUri) }
        .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
      PreviewableAsyncImage(
        imageUrl = item.imageUri,
        placeholderType = "none",
        modifier = Modifier.size(48.dp).clip(CircleShape)
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