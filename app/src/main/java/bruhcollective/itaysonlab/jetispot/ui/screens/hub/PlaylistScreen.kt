package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpMetadataRequester
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpPartnersApi
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.ui.hub.virt.PlaylistEntityView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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
    appBarTitle = viewModel.title.value,
    state = viewModel.state,
    viewModel = viewModel
  ) {
    viewModel.reloadPlaylist(id)
  }
}

@HiltViewModel
class PlaylistViewModel @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val spPartnersApi: SpPartnersApi,
  private val spPlayerServiceManager: SpPlayerServiceManager,
  private val spMetadataRequester: SpMetadataRequester
) : AbsHubViewModel() {
  val title = mutableStateOf("")

  private val _playlistMetadata = mutableStateOf<PlaylistEntityView.ApiPlaylist?>(null)
  val playlistMetadata: PlaylistEntityView.ApiPlaylist? get() = _playlistMetadata.value

  suspend fun loadPlaylist(id: String) = load { loadPlaylistInternal(id) }
  suspend fun reloadPlaylist(id: String) = reload { loadPlaylistInternal(id) }
  suspend fun loadPlaylistInternal(id: String) = PlaylistEntityView.getPlaylistView(id, spSessionManager, spMetadataRequester).also { _playlistMetadata.value = it; title.value = it.playlist.attributes.name; }.hubResponse

  override fun play(data: PlayFromContextData) = play(spPlayerServiceManager, data)
  override suspend fun calculateDominantColor(url: String, dark: Boolean) = calculateDominantColor(spPartnersApi, url, dark)
}