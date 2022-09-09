package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
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
  val scrollBehavior = if (toolbarOptions.alwaysVisible) TopAppBarDefaults.exitUntilCollapsedScrollBehavior() else TopAppBarDefaults.pinnedScrollBehavior()

  when (state) {
    is HubState.Loaded -> {
      Scaffold(topBar = {
        if (toolbarOptions.big) {
          LargeTopAppBar(title = {
            Text(appBarTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
          }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
              Icon(Icons.Rounded.ArrowBack, null)
            }
          }, colors = TopAppBarDefaults.largeTopAppBarColors(), scrollBehavior = scrollBehavior)
        } else {
          SmallTopAppBar(title = {
            Text(appBarTitle, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.alpha(scrollBehavior.state.overlappedFraction))
          }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
              Icon(Icons.Rounded.ArrowBack, null)
            }
          }, colors = if (toolbarOptions.alwaysVisible) TopAppBarDefaults.smallTopAppBarColors() else TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(3.dp)
          ), scrollBehavior = scrollBehavior)
        }
      }, modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection)) { padding ->
        CompositionLocalProvider(LocalHubScreenDelegate provides viewModel) {
          LazyColumn(
            modifier = Modifier
              .fillMaxHeight()
              .let { if (toolbarOptions.alwaysVisible) it.padding(padding) else it }
          ) {
            state.data.apply {
              if (header != null) {
                item(
                  key = header.id,
                  contentType = header.component.javaClass.simpleName,
                ) {
                  HubBinder(header)
                }
              }

              items(body, key = { it.id }, contentType = { it.component.javaClass.simpleName }) {
                Box(modifier = Modifier.animateItemPlacement()) {
                  HubBinder(it)
                }
              }
            }
          }
        }
      }
    }
    is HubState.Error -> PagingErrorPage(exception = state.error, onReload = { scope.launch { reloadFunc() } }, modifier = Modifier.fillMaxSize())
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