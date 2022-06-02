package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun PreviewableAsyncImage (
  imageUrl: String?,
  placeholderType: String?,
  modifier: Modifier
) {
  if (imageUrl.isNullOrEmpty() || imageUrl == "https://i.scdn.co/image/") {
    Box(modifier) {
      ImagePreview(placeholderType, modifier)
    }
  } else {
    val painter = rememberAsyncImagePainter(model = imageUrl, contentScale = ContentScale.Crop)
    val isLoaded = painter.state is AsyncImagePainter.State.Success

    if (isLoaded) {
      Image(painter = painter, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
    } else {
      Box(modifier) {
        ImagePreview(placeholderType, modifier)
        Image(painter = painter, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
      }
    }
  }
}

@Composable
fun PreviewableSyncImage (
  imageBitmap: ImageBitmap?,
  placeholderType: String?,
  modifier: Modifier
) {
  if (imageBitmap != null) {
    Image(bitmap = imageBitmap, contentScale = ContentScale.Crop, contentDescription = null, modifier = modifier)
  } else {
    ImagePreview(placeholderType, modifier)
  }
}

@Stable
@Composable
fun ImagePreview (
  of: String?,
  modifier: Modifier
) {
  ImagePreview(
    if (of != "none") placeholderToIcon(of) else null,
    false,
    modifier
  )
}

@Stable
@Composable
fun ImagePreview (
  of: ImageVector?,
  colorful: Boolean,
  modifier: Modifier
) {
  Surface(tonalElevation = if (colorful) 0.dp else 8.dp, color = if (colorful) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, modifier = modifier) {
    if (of != null) {
      Box(Modifier.fillMaxSize()) {
        Icon(imageVector = of, tint = if (colorful) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, contentDescription = null, modifier = Modifier.fillMaxSize().padding(8.dp))
      }
    }
  }
}

private fun placeholderToIcon (type: String?) = when (type) {
  "artist" -> Icons.Default.Mic
  "album" -> Icons.Default.Album
  "podcasts" -> Icons.Default.Podcasts
  "playlist" -> Icons.Default.PlaylistPlay
  "user" -> Icons.Default.Person
  else -> Icons.Default.Audiotrack
}