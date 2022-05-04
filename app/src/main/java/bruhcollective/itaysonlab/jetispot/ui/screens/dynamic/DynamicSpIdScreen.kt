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
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.HubScreen

@Composable
fun DynamicSpIdScreen(
  navController: NavController,
  type: String?,
  id: String?,
  additionalItem: String? = null
) {
  var dest = SpIdDests.values().firstOrNull {
    it.type == type && it.additionalItem == additionalItem
  }

  if (type == "user" && additionalItem != null) {
    val aiTwo = additionalItem.split(":")
    dest = SpIdDests.values().firstOrNull { it.type == aiTwo[0] }
  }

  if (dest != null) {
    HubScreen(
      navController = navController,
      needContentPadding = false,
      statusBarPadding = additionalItem != null,
      loader = { mgr ->
        dest.provider(this, mgr, if (type == "user" && additionalItem != null) additionalItem.split(":")[1] else id!!)
      })
  } else {
    Box(Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .align(Alignment.Center)
      ) {
        Text(type ?: "type unknown")
        Text(id ?: "id unknown")
        Text(additionalItem ?: "null")
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
  }),

  Playlist("playlist", { mgr, id ->
    mgr.internal.getPlaylistView(id)
  }),
}