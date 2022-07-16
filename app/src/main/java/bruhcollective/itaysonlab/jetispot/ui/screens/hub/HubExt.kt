package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingErrorPage
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.LargeTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubScaffold(
  navController: LambdaNavigationController,
  appBarTitle: String,
  state: HubState,
  viewModel: HubScreenDelegate,
  toolbarOptions: ToolbarOptions = ToolbarOptions(),
  reloadFunc: suspend () -> Unit
) {
  val scope = rememberCoroutineScope()
  val sbd = rememberSplineBasedDecay<Float>()
  val topBarState = rememberEUCScrollBehavior()
  val scrollBehavior = rememberLazyListState()

  when (state) {
    is HubState.Loaded -> {
      Scaffold(
        modifier = Modifier
          .fillMaxSize()
          .nestedScroll(topBarState.nestedScrollConnection)
      ) { padding ->
        Column(
          Modifier
            .fillMaxHeight()
            .scrollable(scrollBehavior, Orientation.Vertical)
        ) {
          // top app bar to fix scrolling in album view
          LargeTopAppBar(
            title = {},
            scrollBehavior = topBarState,
            modifier = Modifier.height(0.dp)
          )

          state.data.apply {
            if (header != null) {
              HubBinder(navController, viewModel, header, scrollBehavior = topBarState)
            }
          }

          LazyColumn {
            state.data.apply {
              items(body, key = { it.id }, contentType = { it.component.javaClass.simpleName }) {
                // album header
                HubBinder(navController, viewModel, it, scrollBehavior = topBarState, albumHeader = true, everythingElse = false)
              }
            }
          }
          LazyColumn(
            modifier = Modifier
              .fillMaxHeight()
              .let { if (toolbarOptions.alwaysVisible) it.padding(padding) else it }
          ) {
            state.data.apply {
              items(body, key = { it.id }, contentType = { it.component.javaClass.simpleName }) {
                // Playlist track list
                HubBinder(navController, viewModel, it, scrollBehavior = topBarState)
              }
            }
          }
        }
      }
    }
    is HubState.Error -> PagingErrorPage(
      exception = state.error,
      onReload = { scope.launch { reloadFunc() } },
      modifier = Modifier.fillMaxSize()
    )
    HubState.Loading -> PagingLoadingPage(Modifier.fillMaxSize())
  }
}

sealed class HubState {
  object Loading: HubState()
  class Error (val error: Exception): HubState()
  class Loaded (val data: HubResponse): HubState()
}

class ToolbarOptions(
  val big: Boolean = false,
  val alwaysVisible: Boolean = false
)