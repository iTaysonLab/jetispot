package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.api.SpPartnersApi
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.core.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import xyz.gianlu.librespot.metadata.AlbumId
import javax.inject.Inject

@Composable
fun AlbumScreen(
  id: String,
  viewModel: AlbumViewModel = hiltViewModel()
) {
  LaunchedEffect(Unit) {
    viewModel.load(id)
  }

  HubScaffold(
    appBarTitle = viewModel.title.value,
    state = viewModel.state,
    viewModel = viewModel
  ) {
    viewModel.reload(id)
  }
}

@HiltViewModel
class AlbumViewModel @Inject constructor(
  private val spInternalApi: SpInternalApi,
  private val spPartnersApi: SpPartnersApi,
  private val spPlayerServiceManager: SpPlayerServiceManager,
  private val spDao: LocalCollectionDao,
  private val spCollectionManager: SpCollectionManager
) : AbsHubViewModel() {
  val title = mutableStateOf("")

  var objId = ""
  var spId = ""

  private fun subscribeOnAlbum(id: String) {
    viewModelScope.launch {
      spDao.subscribeOnAlbum(objId).stateIn(this).collect {
        Log.d("AlbumViewModel", "state = $it")
        mainAddedState.value = it.isNotEmpty()
      }
    }
  }
  override fun toggleMainObjectAddedState() {
    viewModelScope.launch {
      spCollectionManager.toggle(spId)
      mainAddedState.value = !mainAddedState.value
    }
  }

  suspend fun load(id: String) = load {

    subscribeOnAlbum(id)
    loadInternal(id)
  }

  suspend fun reload(id: String) = reload { loadInternal(id) }
  private suspend fun loadInternal(id: String) = spInternalApi.getAlbumView(id).also {
    spId = AlbumId.fromBase62(id).toSpotifyUri()
    objId = AlbumId.fromBase62(id).hexId()
    title.value = it.title ?: ""
  }

  override fun play(data: PlayFromContextData) = play(spPlayerServiceManager, data)
  override suspend fun calculateDominantColor(url: String, dark: Boolean) = calculateDominantColor(spPartnersApi, url, dark)
}