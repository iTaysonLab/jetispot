package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.api.SpPartnersApi
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.core.util.Log
import bruhcollective.itaysonlab.jetispot.ui.ext.collectAsStateLifecycleAware
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import xyz.gianlu.librespot.metadata.AlbumId
import javax.inject.Inject

@Composable
fun AlbumScreen(
  navController: LambdaNavigationController,
  id: String,
  viewModel: AlbumViewModel = hiltViewModel()
) {
  LaunchedEffect(Unit) {
    viewModel.load(id)
  }

  val addedState by viewModel.createAddedFlow(id).collectAsStateLifecycleAware(initial = emptyList())

  Log.d("AlbumScreen", "state = ${LocalLifecycleOwner.current.lifecycle.currentState}, state = ${addedState}")
  if (LocalLifecycleOwner.current.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
    viewModel.mainAddedState.value = addedState.isNotEmpty()
  }

  HubScaffold(
    navController = navController,
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
  private val spDao: LocalCollectionDao
) : AbsHubViewModel() {
  val title = mutableStateOf("")
  var addedFlow: Flow<List<CollectionAlbum>>? = null

  fun createAddedFlow(id: String): Flow<List<CollectionAlbum>> {
    if (addedFlow == null) { addedFlow = spDao.subscribeOnAlbum(AlbumId.fromBase62(id).hexId()) }
    return addedFlow!!
  }

  suspend fun load(id: String) = load { createAddedFlow(id); loadInternal(id) }
  suspend fun reload(id: String) = reload { loadInternal(id) }
  private suspend fun loadInternal(id: String) = spInternalApi.getAlbumView(id).also { title.value = it.title ?: "" }

  override fun play(data: PlayFromContextData) = play(spPlayerServiceManager, data)
  override suspend fun calculateDominantColor(url: String, dark: Boolean) = calculateDominantColor(spPartnersApi, url, dark)
}