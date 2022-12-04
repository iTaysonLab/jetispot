package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.core.objs.hub.isGrid
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.core.util.UpdateUtil
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.screens.Dialog
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingErrorPage
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun HubScreen(
  needContentPadding: Boolean = true,
  loader: suspend SpInternalApi.() -> HubResponse,
  viewModel: HubScreenViewModel = hiltViewModel(),
  statusBarPadding: Boolean = false,
  onAppBarTitleChange: (String) -> Unit = {},
) {
  val scope = rememberCoroutineScope()
  var latestRelease by remember { mutableStateOf(UpdateUtil.LatestRelease()) }
  var showUpdateDialog by rememberSaveable { mutableStateOf(false) }

  val navController = LocalNavigationController.current

  viewModel.needContentPadding = needContentPadding

  LaunchedEffect(Unit) {
    viewModel.load(onAppBarTitleChange, loader)
  }

  if(showUpdateDialog) {
    navController.navigate(Dialog.UpdateAvailable)
  }

  when (viewModel.state) {
    is HubScreenViewModel.State.Loaded -> {
      CompositionLocalProvider(LocalHubScreenDelegate provides viewModel) {
        LazyVerticalGrid(
          contentPadding = PaddingValues(if (needContentPadding) 16.dp else 0.dp),
          verticalArrangement = Arrangement.spacedBy(if (needContentPadding) 8.dp else 0.dp),
          horizontalArrangement = Arrangement.spacedBy(if (needContentPadding) 8.dp else 0.dp),
          columns = GridCells.Fixed(2),
          modifier = if (statusBarPadding) Modifier
            .fillMaxSize()
            .statusBarsPadding() else Modifier.fillMaxSize()
        ) {
          (viewModel.state as HubScreenViewModel.State.Loaded).data.apply {
            if (header != null) {
              item(
                key = header.id,
                span = {
                  GridItemSpan(2)
                },
                contentType = header.component.javaClass.simpleName,
              ) {
                HubBinder(header)
              }
            }

            body.forEach { item ->
              if (item.component.isGrid() && !item.children.isNullOrEmpty()) {
                items(item.children, key = { dItem -> dItem.id }, contentType = {
                  item.component.javaClass.simpleName
                }) { cItem ->
                  HubBinder(cItem)
                }
              } else {
                item(span = {
                  GridItemSpan(if (item.component.isGrid()) 1 else 2)
                }, key = item.id, contentType = {
                  item.component.javaClass.simpleName
                }) {
                  HubBinder(item, isRenderingInGrid = item.component.isGrid())
                }
              }
            }
          }
        }
      }
    }
    
    is HubScreenViewModel.State.Error -> PagingErrorPage(exception = (viewModel.state as HubScreenViewModel.State.Error).error, onReload = { scope.launch { viewModel.reload(onAppBarTitleChange, loader) } }, modifier = Modifier.fillMaxSize())
    HubScreenViewModel.State.Loading -> PagingLoadingPage(modifier = Modifier.fillMaxSize())
  }
}

@HiltViewModel
class HubScreenViewModel @Inject constructor(
  private val spInternalApi: SpInternalApi,
  private val spPlayerServiceManager: SpPlayerServiceManager,
  private val spCollectionManager: SpCollectionManager
) : ViewModel(), HubScreenDelegate {
  private val _state = mutableStateOf<State>(State.Loading)
  val state: State get() = _state.value

  val nullState = mutableStateOf(false)
  override fun getMainObjectAddedState() = nullState

  // no state handle needed
  var needContentPadding: Boolean = false

  suspend fun load(chg: (String) -> Unit, loader: suspend SpInternalApi.() -> HubResponse) {
    _state.value = try {
      State.Loaded(spInternalApi.loader().also { chg(it.title ?: "") })
    } catch (e: Exception) {
      e.printStackTrace()
      State.Error(e)
    }
  }

  suspend fun reload(chg: (String) -> Unit, loader: suspend SpInternalApi.() -> HubResponse) {
    _state.value = State.Loading
    load(chg, loader)
  }

  override fun play(data: PlayFromContextData) {
    spPlayerServiceManager.play(data.uri, data.player)
  }

  override fun isSurroundedWithPadding() = needContentPadding

  override suspend fun calculateDominantColor(url: String, dark: Boolean) = Color.Transparent

  override suspend fun getLikedSongsCount(artistId: String): Int {
    return spCollectionManager.tracksByArtist(artistId).size
  }

  sealed class State {
    class Loaded(val data: HubResponse) : State()
    class Error(val error: Exception) : State()
    object Loading : State()
  }
}