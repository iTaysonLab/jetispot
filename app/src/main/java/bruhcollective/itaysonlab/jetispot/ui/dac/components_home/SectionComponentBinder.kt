package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.ui.ext.dynamicUnpack
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import com.spotify.home.dac.component.v1.proto.*
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSnapperApi::class)
@Composable
fun SectionComponentBinder(
  item: SectionComponent
) {
  val navController = LocalNavigationController.current

  val list = item.componentsList.map { it.dynamicUnpack() }
  val lazyListState = rememberLazyListState()

  Box(
    Modifier
      .wrapContentSize()
      .padding(horizontal = 16.dp)
      .clip(shape = RoundedCornerShape(34.dp))
  ) {
    Card(
      shape = RoundedCornerShape(34.dp)
    ){
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp),
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(
          lazyListState,
          snapOffsetForItem = SnapOffsets.Start,
          decayAnimationSpec = rememberSplineBasedDecay(),
          springAnimationSpec = spring(dampingRatio = 0.001f, stiffness = 10f)
        )
      ) {
        items(list) { listItem ->
          when (listItem) {
            is AlbumCardMediumComponent -> MediumCard(
              title = listItem.title,
              subtitle = listItem.subtitle,
              navigateUri = listItem.navigateUri,
              imageUri = listItem.imageUri,
              imagePlaceholder = "album"
            )

            is PlaylistCardMediumComponent -> MediumCard(
              title = listItem.title,
              subtitle = listItem.subtitle,
              navigateUri = listItem.navigateUri,
              imageUri = listItem.imageUri,
              imagePlaceholder = "playlist"
            )

            is ArtistCardMediumComponent -> MediumCard(
              title = listItem.title,
              subtitle = listItem.subtitle,
              navigateUri = listItem.navigateUri,
              imageUri = listItem.imageUri,
              imagePlaceholder = "artist"
            )

            is EpisodeCardMediumComponent -> MediumCard(
              title = listItem.title,
              subtitle = listItem.subtitle,
              navigateUri = listItem.navigateUri,
              imageUri = listItem.imageUri,
              imagePlaceholder = "podcasts"
            )

            is ShowCardMediumComponent -> MediumCard(
              title = listItem.title,
              subtitle = listItem.subtitle,
              navigateUri = listItem.navigateUri,
              imageUri = listItem.imageUri,
              imagePlaceholder = "podcasts"
            )
          }
        }
      }
    }

    Spacer(
      Modifier
        .fillMaxWidth(0.04f)
        .height(274.dp)
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color.Transparent,
              MaterialTheme.colorScheme.surfaceVariant
            )
          )
        )
        .align(Alignment.BottomEnd)
    )

    Spacer(
      Modifier
        .fillMaxWidth(0.04f)
        .height(274.dp)
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(
              MaterialTheme.colorScheme.surfaceVariant,
              Color.Transparent
            )
          )
        )
    )
  }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun MediumCard(
  title: String,
  subtitle: String,
  navigateUri: String,
  imageUri: String,
  imagePlaceholder: String
) {
  val navController = LocalNavigationController.current
  Surface(
    color = MaterialTheme.colorScheme.background,
    shape = RoundedCornerShape(20.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .width(172.dp)
        .clickable { navController.navigate(navigateUri) }
        .padding(bottom = 12.dp)
    ) {
      var drawnTitle = false

      // Had to wrap the image in another composable due to weird padding when
      // image couldn't be retrieved
      Surface(Modifier.padding(top = 6.dp)) {
        PreviewableAsyncImage(
          imageUrl = imageUri,
          placeholderType = imagePlaceholder,
          modifier = Modifier
            .size(160.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
        )
      }

      // Title of the card. TODO: Scrolling text
      Column(
        Modifier
          .height(64.dp)
          .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.Center
      ) {
        if (title.isNotEmpty()) {
          drawnTitle = true
          Text(
            title,
            fontSize = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            textAlign = if (imagePlaceholder == "artist") TextAlign.Center else TextAlign.Start
          )
        }

        if (subtitle.isNotEmpty()) {
          Text(
            subtitle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
              .padding(top = if (drawnTitle) 4.dp else 0.dp)
              .fillMaxWidth(),
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
          )
        }
      }
    }
  }
}