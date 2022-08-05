package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.dynamic_blocks.DynamicLikeButton
import bruhcollective.itaysonlab.jetispot.ui.shared.dynamic_blocks.DynamicPlayButton
import com.spotify.dac.player.v1.proto.PlayCommand

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun SmallActionCardBinder(
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
  val navController = LocalNavigationController.current

  Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .height(128.dp)
      .fillMaxWidth()
      .clip(RoundedCornerShape(32.dp))
      .clickable { navController.navigate(navigateUri) }
  ) {
    Row(Modifier.padding(16.dp)) {
      PreviewableAsyncImage(
        imageUrl = imageUri,
        placeholderType = imagePlaceholder,
        modifier = Modifier
          .fillMaxHeight()
          .clip(shape = RoundedCornerShape(16.dp))
          .align(CenterVertically)
          .animateContentSize()
      )

      Column(
        Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Column(Modifier.padding(start = 16.dp)) {
          Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 2,
            style = TextStyle(platformStyle = PlatformTextStyle(false))
          )
          Text(
            text = subtitle,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.7f),
            maxLines = 2,
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            modifier = Modifier.padding(top = 4.dp)
          )
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          DynamicLikeButton(objectUrl = likeUri, modifier = Modifier.size(24.dp))
          DynamicPlayButton(command = playCommand, modifier = Modifier.size(24.dp))
        }
      }
    }
  }
}