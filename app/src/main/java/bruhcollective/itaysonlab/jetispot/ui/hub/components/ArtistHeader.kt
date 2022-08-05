package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.ImageBackgroundTopAppBar
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistHeader(
  item: HubItem,
  scrollBehavior: TopAppBarScrollBehavior,
) {
  val navController = LocalNavigationController.current
  ImageBackgroundTopAppBar(
    title = {
      Text(
        item.text!!.title!!,
        Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
        maxLines = 4
      )
    },
    smallTitle = {
      Text(
        item.text!!.title!!,
        Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    },
    picture = {
      AsyncImage(
        model = item.images?.main?.uri,
        contentDescription = null,
        Modifier
          .fillMaxSize(),
        contentScale = ContentScale.FillWidth
      )
    },
    scrollBehavior = scrollBehavior,
    isLarge = false,
    navigationIcon = {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
      }
    },
    contentPadding = PaddingValues(
      top = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(LocalDensity.current).toDp()
      }
    )
  )
}
