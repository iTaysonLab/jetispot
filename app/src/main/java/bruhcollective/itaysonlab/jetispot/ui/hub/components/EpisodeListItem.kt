package bruhcollective.itaysonlab.jetispot.ui.hub.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Explicit
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import com.spotify.metadata.Metadata
import java.text.DateFormat
import java.util.*

@Composable
fun EpisodeListItem(
    item: HubItem
) {
    val episode = remember { item.custom!!["episode"] as Metadata.Episode }
    val imageUrl =
        remember { SpUtils.getImageUrl(episode.coverImage.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId) }
    val formattedDuration = remember { DateUtils.formatElapsedTime(episode.duration / 1000L) }
    val formattedPublishDate = remember {
        DateFormat.getDateInstance().format(Calendar.getInstance().apply {
            set(
                episode.publishTime.year,
                episode.publishTime.month,
                episode.publishTime.day,
                episode.publishTime.hour,
                episode.publishTime.minute
            )
        }.time)
    }

    Column(
      Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row {
            PreviewableAsyncImage(
                imageUrl = imageUrl, placeholderType = "podcast", modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
            )
            Text(
                text = episode.name,
                modifier = Modifier
                  .padding(start = 16.dp)
                  .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = episode.description,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 13.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(16.dp))

        Row {
            if (episode.explicit) {
                Icon(
                    Icons.Rounded.Explicit,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    contentDescription = null,
                    modifier = Modifier
                      .size(16.dp)
                      .align(Alignment.CenterVertically)
                )
                Text(
                    text = " • ",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }

            Text(
                text = "$formattedDuration • $formattedPublishDate",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        Row {
            IconButton(
                onClick = { /*TODO*/ },
              Modifier
                .offset(y = 2.dp)
                .align(Alignment.CenterVertically)
                .size(28.dp)
            ) {
                Icon(Icons.Rounded.AddCircle, null)
            }

            Spacer(Modifier.width(16.dp))

            IconButton(
                onClick = { /*TODO*/ },
              Modifier
                .offset(y = 2.dp)
                .align(Alignment.CenterVertically)
                .size(28.dp)
            ) {
                Icon(Icons.Rounded.Share, null)
            }

            Spacer(Modifier.weight(1f))

            Box(
              Modifier
                .clickableHub(item)
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null,
                    modifier = Modifier
                      .size(24.dp)
                      .align(Alignment.Center)
                )
            }
        }

        Box(
          Modifier
            .padding(top = 16.dp)
            .background(MaterialTheme.colorScheme.compositeSurfaceElevation(8.dp))
            .fillMaxWidth()
            .height(1.dp)
        ) {}
    }
}