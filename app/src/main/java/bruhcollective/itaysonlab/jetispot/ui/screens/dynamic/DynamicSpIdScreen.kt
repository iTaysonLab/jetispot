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
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.HubScreen

@Composable
fun DynamicSpIdScreen(
    navController: NavController,
    type: String?,
    id: String?,
    additionalItem: String? = null
) {
    val dest = SpIdDests.values().firstOrNull {
        it.type == type &&
        it.additionalItem == additionalItem
    }

  if (dest != null) {
    HubScreen(navController = navController, needContentPadding = false, statusBarPadding = additionalItem != null, loader = {
      dest.provider(this, id!!)
    })
  } else {
    Box(Modifier.fillMaxSize()) {
      Column(modifier = Modifier
        .align(Alignment.Center)) {
        Text(type ?: "type unknown")
        Text(id ?: "id unknown")
      }
    }
  }
}

enum class SpIdDests(
    val type: String,
    val provider: suspend SpApiManager.(String) -> HubResponse,
    val additionalItem: String? = null
) {
    Artist("artist", { id ->
        internal.getArtistView(id)
    }),

    Releases("artist", { id ->
        internal.getReleasesView(id)
    }, "releases"),

    Album("album", { id ->
        internal.getAlbumView(id)
    }),

    Genre("genre", { id ->
        internal.getBrowseView(id)
    }),

    Playlist("playlist", { id ->
        internal.getPlaylistView(id)
    }),
}