package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import bruhcollective.itaysonlab.jetispot.ui.shared.dynamic_blocks.DynamicLikeButton
import bruhcollective.itaysonlab.jetispot.ui.shared.dynamic_blocks.DynamicPlayButton
import com.spotify.dac.player.v1.proto.PlayCommand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallActionCardBinder(
  navController: LambdaNavigationController,
  title: String,
  subtitle: String,
  navigateUri: String,
  likeUri: String,
  imageUri: String,
  imagePlaceholder: String,
  playCommand: PlayCommand
) {
  // TODO: possibly background color based on dominant color from the artwork
//  val viewModel: NowPlayingViewModel = hiltViewModel()

  Card(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .height(128.dp)
      .fillMaxWidth()
      .clip(RoundedCornerShape(32.dp))
      .clickable { navController.navigate(navigateUri) }
  ) {
    Row(Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
      PreviewableAsyncImage(
        imageUrl = imageUri,
        placeholderType = imagePlaceholder,
        modifier = Modifier
          .fillMaxHeight()
//          .width(800.dp)
          .padding(bottom = 16.dp)
          .clip(shape = RoundedCornerShape(16.dp))
          .align(CenterVertically)
          .animateContentSize()
      )

      Column(
        Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Column(Modifier.padding(start = 16.dp)) {
          Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
          Subtext(text = subtitle)
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          DynamicLikeButton(objectUrl = likeUri)
          DynamicPlayButton(command = playCommand)
        }
      }
    }
  }
}