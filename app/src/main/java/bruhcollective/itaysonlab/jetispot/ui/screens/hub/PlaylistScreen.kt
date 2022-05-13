package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpApiManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.api.edges.SpInternalApi.ApiPlaylist
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
  navController: NavController,
  id: String,
  viewModel: PlaylistViewModel = hiltViewModel()
) {
  LaunchedEffect(Unit) {
    viewModel.loadPlaylist(id)
    // TODO: Load likes
  }

  HubScaffold(
    navController = navController,
    appBarTitle = viewModel.playlistMetadata?.playlist?.attributes?.name ?: "",
    state = viewModel.state,
    viewModel = viewModel
  ) {
    viewModel.reloadPlaylist(id)
  }
}

@HiltViewModel
class PlaylistViewModel @Inject constructor(
  private val spInternalApi: SpInternalApi,
  private val spApiManager: SpApiManager,
  private val spPlayerServiceManager: SpPlayerServiceManager
) : AbsHubViewModel() {
  private val _playlistMetadata = mutableStateOf<ApiPlaylist?>(null)
  val playlistMetadata: ApiPlaylist? get() = _playlistMetadata.value

  suspend fun loadPlaylist(id: String) = load { loadPlaylistInternal(id) }
  suspend fun reloadPlaylist(id: String) = reload { loadPlaylistInternal(id) }
  suspend fun loadPlaylistInternal(id: String) = spApiManager.internal.getPlaylistView(id).also { _playlistMetadata.value = it }.hubResponse

  override fun play(data: PlayFromContextData) = play(spPlayerServiceManager, data)
  override suspend fun calculateDominantColor(url: String, dark: Boolean) = calculateDominantColor(spApiManager, url, dark)
}