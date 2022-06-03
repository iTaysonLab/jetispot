package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.core.objs.hub.*
import bruhcollective.itaysonlab.jetispot.core.objs.player.*
import dagger.hilt.android.lifecycle.HiltViewModel
import xyz.gianlu.librespot.metadata.ArtistId
import javax.inject.Inject

@Composable
fun LikedSongsScreen(
  navController: LambdaNavigationController,
  id: String,
  fullUri: String,
  viewModel: LikedSongsViewModel = hiltViewModel()
) {
  LaunchedEffect(Unit) {
    viewModel.load(fullUri, id)
  }

  HubScaffold(
    navController = navController,
    appBarTitle = stringResource(R.string.liked_songs),
    state = viewModel.state,
    toolbarOptions = ToolbarOptions(big = true, alwaysVisible = true),
    viewModel = viewModel
  ) {}
}

@HiltViewModel
class LikedSongsViewModel @Inject constructor(
  private val spPlayerServiceManager: SpPlayerServiceManager,
  private val spCollectionManager: SpCollectionManager
) : AbsHubViewModel() {
  override fun play(data: PlayFromContextData) = play(spPlayerServiceManager, data)
  override suspend fun calculateDominantColor(url: String, dark: Boolean) = Color.Transparent

  suspend fun load(fullUri: String, id: String) = load {
    val artistTracks = spCollectionManager.tracksByArtist(ArtistId.fromBase62(id).hexId())

    HubResponse(body = buildList {
      artistTracks.forEach { track ->
        add(HubItem(
          component = HubComponent.PlaylistTrackRow,
          images = HubImages(main = HubImage(uri = "https://i.scdn.co/image/${track.picture}")),
          text = HubText(title = track.name, subtitle = track.rawArtistsData.split("|").joinToString { it.split("=")[1] }),
          events = HubEvents(click = HubEvent.PlayFromContext(
            data = PlayFromContextData(
              track.uri,
              PlayFromContextPlayerData(
                PfcContextData(
                  url = "context://${fullUri}",
                  uri = fullUri
                ),
                PfcOptions(skip_to = PfcOptSkipTo(track_uri = track.uri))
              )
            )
          ))
        ))
      }
    })
  }
}