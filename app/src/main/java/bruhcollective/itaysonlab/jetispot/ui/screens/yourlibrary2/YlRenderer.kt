package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionEntry
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionPinnedItem
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
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
    else -> Text(item.toString())
  }
}

@Composable
fun YLRPinned(
  item: CollectionPinnedItem,
  modifier: Modifier
) {
  Row(modifier) {
    AsyncImage(
      model = "https://i.scdn.co/image/${item.picture}",
      contentDescription = null,
      modifier = Modifier
        .size(64.dp).clip(RoundedCornerShape(8.dp))
    )

    Column(
      Modifier
        .padding(start = 16.dp)
        .align(Alignment.CenterVertically)) {
      Text(text = item.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
      Row(Modifier.padding(top = 4.dp)) {
        Icon(Icons.Default.PushPin, tint = MaterialTheme.colorScheme.primary, contentDescription = null, modifier = Modifier.size(16.dp).align(Alignment.CenterVertically))
        Text(
          text = item.subtitle,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.padding(start = 6.dp).align(Alignment.CenterVertically)
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
  Row(modifier) {
    PreviewableAsyncImage(
      imageUrl = item.picture,
      placeholderType = "playlist",
      modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp))
    )

    Column(
      Modifier
        .padding(start = 16.dp)
        .align(Alignment.CenterVertically)) {
      Text(text = item.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
      Text(
        text = item.ownerUsername,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(top = 4.dp)
      )
    }
  }
}