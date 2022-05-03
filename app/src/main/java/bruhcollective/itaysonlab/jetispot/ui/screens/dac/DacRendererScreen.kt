package bruhcollective.itaysonlab.jetispot.ui.screens.dac

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpApiManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.ui.dac.DacRender
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.LargeTopAppBar
import com.google.protobuf.Any
import com.google.protobuf.Message
import com.spotify.dac.api.components.VerticalListComponent
import com.spotify.dac.api.v1.proto.DacResponse
import com.spotify.home.dac.component.v1.proto.HomePageComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// generally just a HubScreen with simplifed code and DAC arch usage
// DAC is something like another ServerSideUI from Spotify
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DacRendererScreen(
  navController: NavController,
  title: String,
  loader: suspend SpInternalApi.() -> DacResponse,
  viewModel: DacViewModel = hiltViewModel()
) {
  val scrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    viewModel.load(loader)
  }

  when (viewModel.state) {
    is DacViewModel.State.Loaded -> {
      Scaffold(topBar = {
        LargeTopAppBar(title = {
          Text(title)
        }, navigationIcon = {
          Icon(Icons.Default.ArrowBack, null,
            Modifier
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = {
                  navController.popBackStack()
                }
              )
              .padding(horizontal = 16.dp))
        }, contentPadding = PaddingValues(top = with(LocalDensity.current) { WindowInsets.statusBars.getTop(
          LocalDensity.current).toDp() }), scrollBehavior = scrollBehavior)
      }, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) { padding ->
        LazyColumn(
          modifier = Modifier.fillMaxHeight().padding(padding)
        ) {
          (viewModel.state as DacViewModel.State.Loaded).data.apply {
            val cmBind: (List<Any>) -> Unit = { componentsList ->
              items(componentsList) { item ->
                var exception: Exception? = null
                var unpackedItem: Message? = null

                try {
                  unpackedItem = item.dynamicUnpack()
                } catch (e: Exception) {
                  exception = e
                }

                if (unpackedItem == null && exception != null) {
                  Text("DAC rendering error: ${exception.message}\n\n${exception.stackTraceToString()}")
                } else if (unpackedItem != null) {
                  DacRender(navController, unpackedItem)
                }

                Spacer(modifier = Modifier.height(12.dp))
              }
            }

            when (this) {
              is VerticalListComponent -> cmBind(this.componentsList)
              is HomePageComponent -> cmBind(this.componentsList)
            }
          }
        }
      }
    }
    is DacViewModel.State.Error -> {
      Box(Modifier.fillMaxSize()) {

        Column(
          Modifier
            .align(Alignment.Center)
        ) {
          Icon(
            Icons.Default.Error, contentDescription = null, modifier = Modifier
              .align(Alignment.CenterHorizontally)
              .size(56.dp)
              .padding(bottom = 12.dp)
          )
          Text(
            "An error occurred while loading the page.",
            modifier = Modifier.align(Alignment.CenterHorizontally)
          )
        }

        OutlinedButton(
          onClick = {
            scope.launch { viewModel.reload(loader) }
          }, modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 16.dp)
        ) {
          Text("Reload")
        }
      }
    }

    DacViewModel.State.Loading -> {
      Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(
          modifier = Modifier
            .align(Alignment.Center)
            .size(56.dp)
        )
      }
    }
  }
}

@HiltViewModel
class DacViewModel @Inject constructor(
  private val spInternalApi: SpInternalApi,
  private val spPlayerServiceManager: SpPlayerServiceManager
) : ViewModel() {
  private val _state = mutableStateOf<State>(State.Loading)
  val state: State get() = _state.value

  suspend fun load(loader: suspend SpInternalApi.() -> DacResponse) {
    _state.value = try {
      val unpackedRaw = spInternalApi.loader()
      val unpackedMessage = unpackedRaw.component.dynamicUnpack()
      State.Loaded(unpackedMessage)
    } catch (e: Exception) {
      e.printStackTrace()
      State.Error(e)
    }
  }

  suspend fun reload(loader: suspend SpInternalApi.() -> DacResponse) {
    _state.value = State.Loading
    load(loader)
  }

  sealed class State {
    class Loaded(val data: Message) : State()
    class Error(val error: Exception) : State()
    object Loading : State()
  }
}

fun Any.dynamicUnpack() = unpack(Class.forName(typeUrl.split("/")[1].let {
  if (!it.startsWith("com.spotify")) "com.spotify.${it}" else it
}) as Class<out Message>)
