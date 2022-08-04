package bruhcollective.itaysonlab.jetispot.ui.bottomsheets.jump_to_artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import com.spotify.metadata.Metadata
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.ArtistId

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
      .background(MaterialTheme.colorScheme.compositeSurfaceElevation(8.dp))
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .navigationBarsPadding()
  ) {
    Text(
      text = "Choose an artist",
      fontSize = 22.sp,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 16.dp)
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
          MediumText(text = artist.second, color = MaterialTheme.colorScheme.onSurface)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = stringResource(id = artist.third),
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}