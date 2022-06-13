package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import bruhcollective.itaysonlab.jetispot.core.SpMetadataRequester
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.api.SpPartnersApi
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.hub.virt.ShowEntityView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun PodcastShowScreen(
  navController: LambdaNavigationController,
  id: String,
  viewModel: PodcastShowViewModel = hiltViewModel()
) {
  LaunchedEffect(Unit) {
    viewModel.load(id)
  }

  HubScaffold(
    navController = navController,
    appBarTitle = viewModel.title.value,
    state = viewModel.state,
    viewModel = viewModel
  ) {
    viewModel.reload { viewModel.load(id) }
  }
}

@HiltViewModel
class PodcastShowViewModel @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val spPartnersApi: SpPartnersApi,
  private val spPlayerServiceManager: SpPlayerServiceManager,
  private val spMetadataRequester: SpMetadataRequester
) : AbsHubViewModel() {
  val title = mutableStateOf("")

  suspend fun load(id: String) = ShowEntityView.create(spSessionManager, spMetadataRequester, id).also { title.value = it.title ?: "" }

  override fun play(data: PlayFromContextData) = play(spPlayerServiceManager, data)
  override suspend fun calculateDominantColor(url: String, dark: Boolean) = calculateDominantColor(spPartnersApi, url, dark)
}