package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.ext.dynamicUnpack
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.navClickable
import com.spotify.home.dac.component.v1.proto.*

@Composable
fun ShortcutsBinder(
    item: ShortcutsSectionComponent
) {
    item.shortcutsList.map { it.dynamicUnpack() }.chunked(2).forEachIndexed { idx, pairs ->
        Row(
          Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = if (idx != item.shortcutsList.lastIndex / 2) 8.dp else 0.dp)
        ) {
            pairs.forEachIndexed { xIdx, xItem ->
                Box(
                  Modifier
                    .weight(1f)
                    .padding(end = if (xIdx == 0 && pairs.size == 2) 8.dp else 0.dp)) {
                    when (xItem) {
                        is AlbumCardShortcutComponent -> ShortcutComponentBinder(
                            xItem.navigateUri,
                            xItem.imageUri,
                            "album",
                            xItem.title
                        )
                        is PlaylistCardShortcutComponent -> ShortcutComponentBinder(
                            xItem.navigateUri,
                            xItem.imageUri,
                            "playlist",
                            xItem.title
                        )
                        is ShowCardShortcutComponent -> ShortcutComponentBinder(
                            xItem.navigateUri,
                            xItem.imageUri,
                            "podcasts",
                            xItem.title
                        )
                        is ArtistCardShortcutComponent -> ShortcutComponentBinder(
                            xItem.navigateUri,
                            xItem.imageUri,
                            "artist",
                            xItem.title
                        )
                        is EpisodeCardShortcutComponent -> ShortcutComponentBinder(
                            xItem.navigateUri,
                            xItem.imageUri,
                            "podcasts",
                            xItem.title
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutComponentBinder(
    navigateUri: String,
    imageUrl: String,
    imagePlaceholder: String,
    title: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(
                3.dp
            )
        ), modifier = Modifier
        .height(56.dp)
        .fillMaxWidth()
    ) {
        Row(Modifier.navClickable { navController ->
            navController.navigate(navigateUri)
        }) {
            PreviewableAsyncImage(
                imageUrl = imageUrl,
                placeholderType = imagePlaceholder,
                modifier = Modifier.size(56.dp)
            )
            Text(
                title,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                  .align(
                    Alignment.CenterVertically
                  )
                  .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

