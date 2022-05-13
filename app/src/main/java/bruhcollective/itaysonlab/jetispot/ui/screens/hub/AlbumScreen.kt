package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpApiManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.ui.ext.collectAsStateLifecycleAware
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import xyz.gianlu.librespot.metadata.AlbumId
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
  navController: NavController,
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
  private val spApiManager: SpApiManager,
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
  override suspend fun calculateDominantColor(url: String, dark: Boolean) = calculateDominantColor(spApiManager, url, dark)
}