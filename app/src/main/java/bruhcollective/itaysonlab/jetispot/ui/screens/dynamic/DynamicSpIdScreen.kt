package bruhcollective.itaysonlab.jetispot.ui.screens.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpApiManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.ui.screens.config.ConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.history.ListeningHistoryScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.HubScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.PlaylistScreen

@Composable
fun DynamicSpIdScreen(
  navController: NavController,
  uri: String,
  fullUri: String,
) {
  var uriSeparated = uri.split(":")
  if (uriSeparated[0] == "user") uriSeparated = uriSeparated.drop(2)
  val id = uriSeparated.getOrElse(1) { "" }

  when (uriSeparated[0]) {
    "album", "artist", "genre" -> HubScreen(navController, needContentPadding = false, loader = {
      if (uriSeparated.getOrNull(2) == "releases") {
        getReleasesView(id)
      } else {
        when (uriSeparated[0]) {
          "album" -> getAlbumView(id)
          "artist" -> getArtistView(id)
          "genre" -> getBrowseView(id)
          else -> error("block issue")
        }
      }
    })

    "playlist" -> PlaylistScreen(navController, id)
    "config" -> ConfigScreen(navController)

    "internal" -> {
      when (id) {
        "listeninghistory" -> ListeningHistoryScreen(navController)
      }
    }

    else -> {
      Box(Modifier.fillMaxSize()) {
        Column(
          modifier = Modifier
            .align(Alignment.Center)
        ) {
          Text(fullUri)
        }
      }
    }
  }
}

enum class SpIdDests(
  val type: String,
  val provider: suspend SpInternalApi.(SpApiManager, String) -> HubResponse,
  val additionalItem: String? = null
) {
  Artist("artist", { _, id ->
    getArtistView(id)
  }),

  Releases("artist", { _, id ->
    getReleasesView(id)
  }, "releases"),

  Album("album", { _, id ->
    getAlbumView(id)
  }),

  Genre("genre", { _, id ->
    getBrowseView(id)
  })
}