package bruhcollective.itaysonlab.jetispot.ui.bottomsheets

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying.NowPlayingViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.MarqueeText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import com.spotify.metadata.Metadata

@Composable
fun MoreOptionsBottomSheet(
    trackName: String,
    artistName: String,
    artworkUrl: String,
) {
    val decodedUrl = "https://i.scdn.co/image/${artworkUrl}"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.compositeSurfaceElevation(8.dp))
            .navigationBarsPadding()
    ) {
        Divider(
            modifier = Modifier
                .width(32.dp)
                .padding(vertical = 14.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally),
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.more_options),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedCard(modifier = Modifier.clip(RoundedCornerShape(8.dp)), elevation = CardDefaults.cardElevation(8.dp)) {
                PreviewableAsyncImage(
                    imageUrl = decodedUrl,
                    placeholderType = "track",
                    modifier = Modifier.size(64.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                MarqueeText(
                    text = trackName,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                MarqueeText(
                    text = artistName,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            modifier = Modifier
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 12.dp),
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)
        )
        Column(modifier = Modifier) {
            actionButton(
                icon = Icons.Rounded.Person,
                text = stringResource(id = R.string.go_to_artist),
                onClick = {})
            buttonsDivider()
            actionButton(
                icon = Icons.Filled.Star,
                text = stringResource(id = R.string.save_to_your_music),
                onClick = {})
            buttonsDivider()
            actionButton(
                icon = Icons.Rounded.Share,
                text = stringResource(id = R.string.share),
                onClick = {})
        }
    }
}

@Composable
private fun actionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.padding()) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(24.dp)
            )
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun buttonsDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)
    )
}

@Preview
@Composable
fun actionButtonPreview() {
    actionButton(icon = Icons.Default.Star, text = "Go to song artist", onClick = {})
}