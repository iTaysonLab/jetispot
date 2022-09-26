package bruhcollective.itaysonlab.jetispot.ui.screens.dac

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.proto.ErrorComponent
import bruhcollective.itaysonlab.jetispot.ui.dac.DacRender
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.FilterComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.ext.dynamicUnpack
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingErrorPage
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import com.google.protobuf.Any
import com.google.protobuf.Message
import com.spotify.dac.api.components.VerticalListComponent
import com.spotify.dac.api.v1.proto.DacResponse
import com.spotify.home.dac.component.experimental.v1.proto.FilterComponent
import com.spotify.home.dac.component.v1.proto.HomePageComponent
import com.spotify.home.dac.component.v1.proto.ToolbarComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// generally just a HubScreen with simplifed code and DAC arch usage
// DAC is something like another ServerSideUI from Spotify
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DacRendererScreen(
  title: String,
  fullscreen: Boolean = false,
  loader: suspend SpInternalApi.(String) -> DacResponse,
  viewModel: DacViewModel = hiltViewModel()
) {
  val navController = LocalNavigationController.current

  val scrollBehavior = if (!fullscreen) TopAppBarDefaults.exitUntilCollapsedScrollBehavior() else TopAppBarDefaults.pinnedScrollBehavior()
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    viewModel.load(loader)
  }

  when (viewModel.state) {
    is DacViewModel.State.Loaded -> {
      Scaffold(topBar = {
        if (fullscreen) {
          (viewModel.state as? DacViewModel.State.Loaded)?.sticky?.let { msg ->
            DacRender(msg)
          }
        } else {
          LargeTopAppBar(title = {
            Text(title)
          }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
              Icon(Icons.Rounded.ArrowBack, null)
            }
          }, scrollBehavior = scrollBehavior)
        }
      }, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), contentWindowInsets = WindowInsets(top = 0.dp)) { padding ->
        LazyColumn(
          modifier = Modifier
            .fillMaxHeight()
            .padding(padding)
        ) {
          (viewModel.state as? DacViewModel.State.Loaded)?.apply {
            items(data) { item ->
              if (item is FilterComponent) {
                FilterComponentBinder(item, viewModel.facet) { nf ->
                  scope.launch {
                    viewModel.facet = nf
                    viewModel.reload(loader)
                  }
                }
              } else {
                DacRender(item)
              }
            }

            item {
              Spacer(modifier = Modifier.height(8.dp))
            }
          }
        }
      }
    }

    is DacViewModel.State.Error -> {
      PagingErrorPage(exception = (viewModel.state as DacViewModel.State.Error).error, onReload = { scope.launch { viewModel.reload(loader) } }, modifier = Modifier.fillMaxSize())
    }

    DacViewModel.State.Loading -> {
      PagingLoadingPage(Modifier.fillMaxSize())
    }
  }
}

@HiltViewModel
class DacViewModel @Inject constructor(
  private val spInternalApi: SpInternalApi
) : ViewModel() {
  var facet = "default"

  private val _state = mutableStateOf<State>(State.Loading)
  val state: State get() = _state.value

  suspend fun load(loader: suspend SpInternalApi.(String) -> DacResponse) {
    _state.value = try {
      val (sticky, list) = withContext(Dispatchers.Default) {
        val messages = parseMessages(when (val protoList = spInternalApi.loader(facet).component.dynamicUnpack()) {
          is VerticalListComponent -> protoList.componentsList
          is HomePageComponent -> protoList.componentsList
          else -> error("Invalid root for DAC renderer! Found: ${protoList.javaClass.simpleName}")
        })

        if (messages.first() is ToolbarComponent) {
          messages.first() to messages.drop(1)
        } else {
          null to messages
        }
      }

      State.Loaded(sticky, list)
    } catch (e: Exception) {
      e.printStackTrace()
      State.Error(e)
    }
  }

  private fun parseMessages(list: List<Any>): List<Message> = list.map { item ->
    try {
      item.dynamicUnpack()
    } catch (e: ClassNotFoundException) {
      ErrorComponent.newBuilder().setType(ErrorComponent.ErrorType.UNSUPPORTED).setMessage(e.message).build()
    } catch (e: java.lang.Exception) {
      ErrorComponent.newBuilder().setType(ErrorComponent.ErrorType.GENERIC_EXCEPTION).setMessage(e.message + "\n\n" + e.stackTraceToString()).build()
    }
  }

  suspend fun reload(loader: suspend SpInternalApi.(String) -> DacResponse) {
    _state.value = State.Loading
    load(loader)
  }

  sealed class State {
    class Loaded(val sticky: Message?, val data: List<Message>) : State()
    class Error(val error: Exception) : State()
    object Loading : State()
  }
}
