package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import coil.compose.AsyncImage
import com.spotify.metadata.Metadata

@Composable
fun ShowHeader(
    item: HubItem
) {
    val show = remember { item.custom!!["show"] as Metadata.Show }
    val imageUrl =
        remember { SpUtils.getImageUrl(show.coverImage.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId) }

    Column {
        Box(
          Modifier
            .fillMaxWidth()
            .height(240.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )

            Box(
              Modifier
                .background(
                  brush = Brush.verticalGradient(
                    colors = listOf(
                      Color.Transparent,
                      MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                      MaterialTheme.colorScheme.surface
                    )
                  )
                )
                .fillMaxSize()
            )

            MediumText(
                text = show.name,
                fontSize = 48.sp,
                lineHeight = 52.sp,
                maxLines = 2,
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(horizontal = 16.dp)
                  .padding(bottom = 8.dp)
            )
        }

        Text(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            text = show.description, modifier = Modifier
                .padding(horizontal = 16.dp)
        )
    }
}