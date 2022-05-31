package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.shared.ControllableScaffold
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingErrorPage
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.SmallTopAppBar
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.LargeTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubScaffold(
  navController: NavController,
  appBarTitle: String,
  state: HubState,
  viewModel: HubScreenDelegate,
  toolbarOptions: ToolbarOptions = ToolbarOptions(),
  reloadFunc: suspend () -> Unit
) {
  val scope = rememberCoroutineScope()
  val sbd = rememberSplineBasedDecay<Float>()
  val scrollBehavior = remember { if (toolbarOptions.alwaysVisible) TopAppBarDefaults.exitUntilCollapsedScrollBehavior(sbd) else TopAppBarDefaults.pinnedScrollBehavior() }

  when (state) {
    is HubState.Loaded -> {
      ControllableScaffold(topBar = {
        if (toolbarOptions.big) {
          LargeTopAppBar(title = {
            Text(appBarTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
          }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
              Icon(Icons.Default.ArrowBack, null)
            }
          }, colors = TopAppBarDefaults.largeTopAppBarColors(), contentPadding = PaddingValues(top = with(LocalDensity.current) { WindowInsets.statusBars.getTop(
            LocalDensity.current).toDp() }), scrollBehavior = scrollBehavior)
        } else {
          SmallTopAppBar(title = {
            Text(appBarTitle, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.alpha(scrollBehavior.scrollFraction))
          }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
              Icon(Icons.Default.ArrowBack, null)
            }
          }, colors = if (toolbarOptions.alwaysVisible)  TopAppBarDefaults.smallTopAppBarColors() else TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(3.dp)
          ), contentPadding = PaddingValues(top = with(LocalDensity.current) { WindowInsets.statusBars.getTop(
            LocalDensity.current).toDp() }), scrollBehavior = scrollBehavior)
        }
      }, drawContentUnderTopBar = !toolbarOptions.alwaysVisible, modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection)) { padding ->
        LazyColumn(
          modifier = Modifier
            .fillMaxHeight()
            .padding(padding)
        ) {
          state.data.apply {
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
    is HubState.Error -> PagingErrorPage(onReload = { scope.launch { reloadFunc() } }, modifier = Modifier.fillMaxSize())
    HubState.Loading -> PagingLoadingPage(Modifier.fillMaxSize())
  }
}

sealed class HubState {
  object Loading: HubState()
  class Error (val ex: Exception): HubState()
  class Loaded (val data: HubResponse): HubState()
}

class ToolbarOptions(
  val big: Boolean = false,
  val alwaysVisible: Boolean = false
)