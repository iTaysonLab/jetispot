package bruhcollective.itaysonlab.jetispot.ui.screens.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.ui.screens.blend.BlendCreateInvitationScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.ConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.history.ListeningHistoryScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.*

@Composable
fun DynamicSpIdScreen(
  navController: NavController,
  uri: String,
  fullUri: String,
) {
  var uriSeparated = uri.split(":")
  if (uriSeparated[0] == "user" && uriSeparated.size > 2) uriSeparated = uriSeparated.drop(2)
  val id = uriSeparated.getOrElse(1) { "" }
  val argument = uriSeparated.getOrElse(2) { "" }

  when (uriSeparated[0]) {
    "genre" -> BrowseScreen(navController = navController, id)

    "artist" -> HubScreen(navController, needContentPadding = false, loader = {
      if (argument == "releases") {
        getReleasesView(id)
      } else {
        getArtistView(id)
      }
    })

    "album" -> AlbumScreen(navController, id)
    "playlist" -> PlaylistScreen(navController, id)
    "config" -> ConfigScreen(navController)
    "radio" -> BrowseRadioScreen(navController)

    "collection" -> when (id) {
      "artist" -> LikedSongsScreen(navController, argument, fullUri)
      "" -> CollectionScreen(navController)
      /* else -> {  TODO  } */
    }

    "internal" -> {
      when (id) {
        "listeninghistory" -> ListeningHistoryScreen(navController)
      }
    }

    "blend" -> when (id) {
      "invitation" -> BlendCreateInvitationScreen(navController)
    }

    else -> {
      Box(Modifier.fillMaxSize()) {
        Column(
          modifier = Modifier
            .align(Alignment.Center)
        ) {
          Text(fullUri)
          Text(uriSeparated.joinToString(":"))
        }
      }
    }
  }
}