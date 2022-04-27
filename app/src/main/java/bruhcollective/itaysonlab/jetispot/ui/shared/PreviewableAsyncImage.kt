package bruhcollective.itaysonlab.jetispot.ui.shared

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
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
  val painter = rememberAsyncImagePainter(model = imageUrl, contentScale = ContentScale.Crop)
  val isLoaded = imageUrl != null && painter.state is AsyncImagePainter.State.Success

  if (isLoaded) {
    Image(painter = painter, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
  } else {
    Box(modifier) {
      ImagePreview(placeholderType, modifier)
      Image(painter = painter, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
    }
  }
}

@Composable
fun PreviewableSyncImage (
  imageData: Bitmap?,
  placeholderType: String?,
  modifier: Modifier
) {
  if (imageData != null) {
    val imageBitmap = remember { imageData.asImageBitmap() }
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
  Surface(tonalElevation = 8.dp, modifier = modifier) {
    Box(Modifier.fillMaxSize()) {
      Icon(imageVector = placeholderToIcon(of), contentDescription = null, Modifier.fillMaxSize().padding(8.dp))
    }
  }
}

private fun placeholderToIcon (type: String?) = when (type) {
  "artist" -> Icons.Default.Mic
  "album" -> Icons.Default.Album
  "podcasts" -> Icons.Default.Podcasts
  "playlist" -> Icons.Default.PlaylistPlay
  else -> Icons.Default.Audiotrack
}