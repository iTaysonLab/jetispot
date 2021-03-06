package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.ext.dynamicUnpack
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import com.spotify.home.dac.component.v1.proto.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortcutsBinder(
  navController: LambdaNavigationController,
  item: ShortcutsSectionComponent
) {
  item.shortcutsList.map { it.dynamicUnpack() }.chunked(2).forEachIndexed { idx, pairs ->
    Row(Modifier.padding(horizontal = 16.dp).padding(bottom = if (idx != item.shortcutsList.lastIndex / 2) 8.dp else 0.dp)) {
      pairs.forEachIndexed { xIdx, xItem ->
        Box(Modifier.weight(1f).padding(end = if (xIdx == 0) 8.dp else 0.dp)) {
          when (xItem) {
            is AlbumCardShortcutComponent -> ShortcutComponentBinder(navController, xItem.navigateUri, xItem.imageUri, "album", xItem.title)
            is PlaylistCardShortcutComponent -> ShortcutComponentBinder(navController, xItem.navigateUri, xItem.imageUri, "playlist", xItem.title)
            is ShowCardShortcutComponent -> ShortcutComponentBinder(navController, xItem.navigateUri, xItem.imageUri, "podcasts", xItem.title)
            is ArtistCardShortcutComponent -> ShortcutComponentBinder(navController, xItem.navigateUri, xItem.imageUri, "artist", xItem.title)
            is EpisodeCardShortcutComponent -> ShortcutComponentBinder(navController, xItem.navigateUri, xItem.imageUri, "podcasts", xItem.title)
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShortcutComponentBinder(
  navController: LambdaNavigationController,
  navigateUri: String,
  imageUrl: String,
  imagePlaceholder: String,
  title: String
) {
  Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(3.dp)), modifier = Modifier.height(56.dp).fillMaxWidth().clickable {
    navController.navigate(navigateUri)
  }) {
    Row {
      PreviewableAsyncImage(imageUrl = imageUrl, placeholderType = imagePlaceholder, modifier = Modifier.size(56.dp))
      Text(title, fontSize = 13.sp, lineHeight = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(
        Alignment.CenterVertically).padding(horizontal = 8.dp))
    }
  }
}

