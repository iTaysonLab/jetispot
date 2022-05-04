package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpApiManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.api.edges.SpInternalApi.ApiPlaylist
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.shared.ControllableScaffold
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingErrorPage
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.SmallTopAppBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
  navController: NavController,
  id: String,
  viewModel: PlaylistViewModel = hiltViewModel()
) {
  val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
  val scope = rememberCoroutineScope()
  val loadFunc: suspend CoroutineScope.() -> Unit = remember {{
    viewModel.loadPlaylist(id)
    // TODO: Load likes
  }}

  LaunchedEffect(Unit) {
    loadFunc()
  }

  when (viewModel.state) {
    is PlaylistViewModel.State.Loaded -> {
      ControllableScaffold(topBar = {
        SmallTopAppBar(title = {
          Text(viewModel.playlistMetadata.playlist.attributes.name, Modifier.alpha(scrollBehavior.scrollFraction))
        }, navigationIcon = {
          IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, null)
          }
        }, colors = TopAppBarDefaults.smallTopAppBarColors(
          containerColor = Color.Transparent,
          scrolledContainerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(3.dp)
        ), contentPadding = PaddingValues(top = with(LocalDensity.current) { WindowInsets.statusBars.getTop(LocalDensity.current).toDp() }), scrollBehavior = scrollBehavior)
      }, drawContentUnderTopBar = true, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) { padding ->
        LazyColumn(
          modifier = Modifier
            .fillMaxHeight()
            .padding(padding)
        ) {
          (viewModel.state as PlaylistViewModel.State.Loaded).data.apply {
            if (header != null) {
              item(
                key = header.id,
                contentType = header.component.javaClass.simpleName,
              ) {
                HubBinder(navController, viewModel, header)
              }
            }

            items(body, key = { it.id }, contentType = { it.component.javaClass.simpleName }) {
              HubBinder(navController, viewModel, it)
            }
          }
        }
      }
    }
    is PlaylistViewModel.State.Error -> PagingErrorPage(onReload = { scope.launch(block = loadFunc) }, modifier = Modifier.fillMaxSize())
    PlaylistViewModel.State.Loading -> PagingLoadingPage(Modifier.fillMaxSize())
  }
}

@HiltViewModel
class PlaylistViewModel @Inject constructor(
  private val spInternalApi: SpInternalApi,
  private val spApiManager: SpApiManager,
  private val spPlayerServiceManager: SpPlayerServiceManager
) : ViewModel(), HubScreenDelegate {
  private val _state = mutableStateOf<State>(State.Loading)
  val state: State get() = _state.value

  private val _playlistMetadata = mutableStateOf<ApiPlaylist?>(null)
  val playlistMetadata: ApiPlaylist get() = _playlistMetadata.value!!

  suspend fun loadPlaylist(id: String) {
    load {
      spApiManager.internal.getPlaylistView(id).also { _playlistMetadata.value = it }.hubResponse
    }
  }

  suspend fun load(loader: suspend SpInternalApi.(SpApiManager) -> HubResponse) {
    _state.value = try {
      State.Loaded(spInternalApi.loader(spApiManager))
    } catch (e: Exception) {
      e.printStackTrace()
      State.Error(e)
    }
  }

  suspend fun reload(loader: suspend SpInternalApi.(SpApiManager) -> HubResponse) {
    _state.value = State.Loading
    load(loader)
  }

  override fun play(data: PlayFromContextData) {
    spPlayerServiceManager.play(data.uri, data.player)
  }

  override fun isSurroundedWithPadding() = false

  override suspend fun calculateDominantColor(url: String, dark: Boolean): Color {
    return try {
      val apiResult = spApiManager.partners.getDominantColors(url).data.extractedColors[0].let {
        if (dark) it.colorRaw else it.colorDark
      }.hex

      Color(android.graphics.Color.parseColor(apiResult))
    } catch (e: Exception) {
      // e.printStackTrace()
      Color.Transparent
    }
  }

  sealed class State {
    class Loaded(val data: HubResponse) : State()
    class Error(val error: Exception) : State()
    object Loading : State()
  }
}