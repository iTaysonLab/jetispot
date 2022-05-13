package bruhcollective.itaysonlab.jetispot.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.shared.ControllableScaffold
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingErrorPage
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.LargeTopAppBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListeningHistoryScreen(
  navController: NavController,
  viewModel: HistoryViewModel = hiltViewModel()
) {
  val scrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }
  val scope = rememberCoroutineScope()
  val loadFunc: suspend CoroutineScope.() -> Unit = remember {{
    viewModel.load {
      getListeningHistory()
    }
  }}

  LaunchedEffect(Unit) {
    loadFunc()
  }

  when (viewModel.state) {
    is HistoryViewModel.State.Loaded -> {
      ControllableScaffold(topBar = {
        LargeTopAppBar(title = {
          Text(stringResource(id = R.string.listening_history))
        }, navigationIcon = {
          IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, null)
          }
        }, contentPadding = PaddingValues(top = with(LocalDensity.current) { WindowInsets.statusBars.getTop(LocalDensity.current).toDp() }), scrollBehavior = scrollBehavior)
      }, drawContentUnderTopBar = false, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) { padding ->
        LazyColumn(
          modifier = Modifier
            .fillMaxHeight()
            .padding(padding)
        ) {
          (viewModel.state as HistoryViewModel.State.Loaded).data.apply {
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
    is HistoryViewModel.State.Error -> PagingErrorPage(onReload = { scope.launch(block = loadFunc) }, modifier = Modifier.fillMaxSize())
    HistoryViewModel.State.Loading -> PagingLoadingPage(Modifier.fillMaxSize())
  }
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
  private val spInternalApi: SpInternalApi,
  private val spPlayerServiceManager: SpPlayerServiceManager
) : ViewModel(), HubScreenDelegate {
  private val _state = mutableStateOf<State>(State.Loading)
  val state: State get() = _state.value

  val nullState = mutableStateOf(false)
  override fun getMainObjectAddedState() = nullState

  suspend fun load(loader: suspend SpInternalApi.() -> HubResponse) {
    _state.value = try {
      State.Loaded(spInternalApi.loader())
    } catch (e: Exception) {
      e.printStackTrace()
      State.Error(e)
    }
  }

  suspend fun reload(loader: suspend SpInternalApi.() -> HubResponse) {
    _state.value = State.Loading
    load(loader)
  }

  override fun play(data: PlayFromContextData) {
    spPlayerServiceManager.play(data.uri, data.player)
  }

  override fun isSurroundedWithPadding() = false

  override suspend fun calculateDominantColor(url: String, dark: Boolean) = Color.Transparent

  sealed class State {
    class Loaded(val data: HubResponse) : State()
    class Error(val error: Exception) : State()
    object Loading : State()
  }
}