package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingErrorPage
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HubScaffold(
  appBarTitle: String,
  state: HubState,
  viewModel: HubScreenDelegate,
  toolbarOptions: ToolbarOptions = ToolbarOptions(),
  reloadFunc: suspend () -> Unit
) {
  val navController = LocalNavigationController.current
  val scope = rememberCoroutineScope()
  val topBarState = rememberEUCScrollBehavior()


  when (state) {
    is HubState.Loaded -> {
      Column(
        modifier = Modifier
          .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
          .nestedScroll(topBarState.nestedScrollConnection)
          .fillMaxSize()
      ) {
        CompositionLocalProvider(LocalHubScreenDelegate provides viewModel) {
          if (appBarTitle == stringResource(id = R.string.listening_history)) {
            LargeTopAppBar(
              title = { Text(appBarTitle) },
              navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                  Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
              },
              scrollBehavior = topBarState
            )
          }

          Box {
            Column(Modifier.fillMaxHeight()) {
              // Playlist header
              state.data.apply {
                if (header != null) {
                  HubBinder(header, scrollBehavior = topBarState)
                }
              }

              state.data.apply {
                HubBinder(
                  body[0],
                  scrollBehavior = topBarState,
                  albumHeader = true,
                  everythingElse = false
                )
              }

              LazyColumn(
                modifier = Modifier
                  .fillMaxHeight()
              ) {
                state.data.apply {
                  items(body, key = { it.id }, contentType = { it.component.javaClass.simpleName }) {
                    // Playlist track list
                    HubBinder(it, scrollBehavior = topBarState)
                  }
                }
              }
            }

            // playlist FAB
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                  animateDpAsState(
                    if (topBarState.state.collapsedFraction <= 0.02f) 16.dp else 0.dp,
                    animationSpec = tween(durationMillis = 500)
                  ).value
                )
            ) {
              state.data.apply {
                state.data.header?.let {
                  HubBinder(
                    it,
                    scrollBehavior = topBarState,
                    showFAB = true,
                    everythingElse = false
                  )
                }
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