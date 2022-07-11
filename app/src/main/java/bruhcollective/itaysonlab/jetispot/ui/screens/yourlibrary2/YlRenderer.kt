package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.*
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import bruhcollective.itaysonlab.jetispot.ui.shared.ImagePreview
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import coil.compose.AsyncImage

@Composable
fun YlRenderer(
  item: CollectionEntry,
  modifier: Modifier
) {
  when (item) {
    is CollectionPinnedItem -> YLRPinned(item, modifier)
    is CollectionRootlistItem -> YLRRootlist(item, modifier)
    is CollectionAlbum -> YLRAlbum(item, modifier)
    is CollectionArtist -> YLRArtist(item, modifier)
    else -> Text(item.toString())
  }
}

@Composable
fun YLRPinned(
  item: CollectionPinnedItem,
  modifier: Modifier
) {
  Row(modifier) {
    val isPredef = item.predefType != null

    if (isPredef) {
      ImagePreview(
        if (item.predefType == PredefCeType.COLLECTION) Icons.Rounded.Favorite else Icons.Rounded.Podcasts,
        true,
        modifier = Modifier
          .size(64.dp)
          .clip(RoundedCornerShape(8.dp))
      )
    } else {
      if (item.picture.isEmpty()) {
        ImagePreview(
          Icons.Rounded.Photo,
          false,
          modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(8.dp))
        )
      } else {
        AsyncImage(
          model = "https://i.scdn.co/image/${item.picture}",
          contentDescription = null,
          modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(8.dp))
        )
      }
    }

    Column(
      Modifier
        .padding(start = 16.dp)
        .align(Alignment.CenterVertically)) {
      Text(text = when (item.predefType) {
        PredefCeType.COLLECTION -> stringResource(id = R.string.liked_songs)
        PredefCeType.EPISODES -> stringResource(id = R.string.new_episodes)
        null -> item.name
      }, maxLines = 1, overflow = TextOverflow.Ellipsis)
      Row(Modifier.padding(top = 4.dp)) {
        Icon(Icons.Rounded.PushPin, tint = MaterialTheme.colorScheme.primary, contentDescription = null, modifier = Modifier
          .size(16.dp)
          .align(Alignment.CenterVertically))
        Text(
          text = when (item.predefType) {
            PredefCeType.COLLECTION -> stringResource(id = R.string.liked_songs_desc, item.predefDyn)
            PredefCeType.EPISODES -> stringResource(id = R.string.new_episodes_desc, item.predefDyn)
            null -> item.subtitle
          },
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier
            .padding(start = 6.dp)
            .align(Alignment.CenterVertically)
        )
      }
    }
  }
}

@Composable
fun YLRRootlist(
  item: CollectionRootlistItem,
  modifier: Modifier
) {
  YLRGenericItem(
    picUrl = item.picture,
    picCircle = false,
    picPlaceholder = "playlist",
    title = item.name,
    subtitle = item.ownerUsername,
    modifier = modifier
  )
}

@Composable
fun YLRAlbum(
  item: CollectionAlbum,
  modifier: Modifier
) {
  YLRGenericItem(
    picUrl = "https://i.scdn.co/image/${item.picture}",
    picCircle = false,
    picPlaceholder = "album",
    title = item.name,
    subtitle = item.rawArtistsData.split("|").joinToString { it.split("=")[1] },
    modifier = modifier
  )
}

@Composable
fun YLRArtist(
  item: CollectionArtist,
  modifier: Modifier
) {
  YLRGenericItem(
    picUrl = "https://i.scdn.co/image/${item.picture}",
    picCircle = true,
    picPlaceholder = "artist",
    title = item.name,
    subtitle = null,
    modifier = modifier
  )
}

@Composable
fun YLRGenericItem(
  picUrl: String,
  picCircle: Boolean,
  picPlaceholder: String,
  title: String,
  subtitle: String?,
  modifier: Modifier
) {
  Row(modifier) {
    PreviewableAsyncImage(
      imageUrl = picUrl,
      placeholderType = picPlaceholder,
      modifier = Modifier
        .size(64.dp)
        .clip(if (picCircle) CircleShape else RoundedCornerShape(8.dp))
    )

    Column(
      Modifier
        .padding(start = 16.dp)
        .align(Alignment.CenterVertically)) {
      Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
      if (!subtitle.isNullOrEmpty()) {
        Text(
          text = subtitle,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    }
  }
}
