package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.SubtextOverline
import com.spotify.home.dac.component.v1.proto.RecsplanationHeadingComponent

@Composable
fun RecsplanationHeadingComponentBinder(
  navController: NavController,
  item: RecsplanationHeadingComponent
) {
  Row(Modifier.padding(vertical = 8.dp).clickable {
    navController.navigate(item.navigateUri)
  }.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
    PreviewableAsyncImage(imageUrl = item.imageUri, placeholderType = "none", modifier = Modifier
      .size(48.dp)
      .clip(CircleShape))

    Column(Modifier.padding(horizontal = 12.dp).align(Alignment.CenterVertically)) {
      SubtextOverline(item.subtitle.uppercase(), modifier = Modifier)
      MediumText(item.title, modifier = Modifier.padding(top = 2.dp), fontSize = 21.sp)
    }
  }
}