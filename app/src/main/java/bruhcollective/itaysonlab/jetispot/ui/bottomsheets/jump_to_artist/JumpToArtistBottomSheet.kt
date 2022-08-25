package bruhcollective.itaysonlab.jetispot.ui.bottomsheets.jump_to_artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import com.spotify.metadata.Metadata
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.ArtistId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JumpToArtistBottomSheet(
  data: String
) {
  val navController = LocalNavigationController.current

  val content = remember {
    data.split("|").map {
      Metadata.ArtistWithRole.parseFrom(Utils.hexToBytes(it))
    }.map {
      Triple(
        ArtistId.fromHex(Utils.bytesToHex(it.artistGid)).toSpotifyUri(),
        it.artistName,
        when (it.role) {
          Metadata.ArtistWithRole.ArtistRole.ARTIST_ROLE_MAIN_ARTIST -> R.string.artist_role_main
          Metadata.ArtistWithRole.ArtistRole.ARTIST_ROLE_FEATURED_ARTIST -> R.string.artist_role_feat
          Metadata.ArtistWithRole.ArtistRole.ARTIST_ROLE_REMIXER -> R.string.artist_role_remixer
          Metadata.ArtistWithRole.ArtistRole.ARTIST_ROLE_ACTOR -> R.string.artist_role_actor
          Metadata.ArtistWithRole.ArtistRole.ARTIST_ROLE_COMPOSER -> R.string.artist_role_composer
          Metadata.ArtistWithRole.ArtistRole.ARTIST_ROLE_CONDUCTOR -> R.string.artist_role_conductor
          Metadata.ArtistWithRole.ArtistRole.ARTIST_ROLE_ORCHESTRA -> R.string.artist_role_orchestra
          else -> R.string.artist_role_unknown
        }
      )
    }
  }

  Column(
    Modifier
      .fillMaxWidth()
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

    Text(
      text = "Choose an artist",
      fontSize = 22.sp,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)
    )

    LazyColumn {
      items(content) { artist ->
        Column(
          Modifier
            .clickable {
              navController.popBackStack()
              navController.navigate(artist.first)
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
        ) {
          Text(
            text = artist.second,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = stringResource(id = artist.third),
            fontSize = 16.sp,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
          )
        }
      }
    }
  }
}