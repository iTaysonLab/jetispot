package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpApiManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.objs.hub.*
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun HubScreen (
  navController: NavController,
  needContentPadding: Boolean = true,
  loader: suspend SpApiManager.() -> HubResponse,
  viewModel: HubScreenViewModel = hiltViewModel()
) {
  LaunchedEffect(Unit) {
    viewModel.load(loader)
  }

  if (viewModel.isLoaded) {
    LazyVerticalGrid(contentPadding = PaddingValues(if (needContentPadding) 16.dp else 0.dp), verticalArrangement = Arrangement.spacedBy(if (needContentPadding) 8.dp else 0.dp), horizontalArrangement = Arrangement.spacedBy(if (needContentPadding) 8.dp else 0.dp), columns = GridCells.Fixed(2)) {
      item(span = { GridItemSpan(2) }) {
        Spacer(modifier = Modifier.statusBarsPadding())
      }

      (viewModel.items ?: listOf()).forEach { item ->
        if (item.component.isGrid() && !item.children.isNullOrEmpty()) {
          items(item.children, key = { dItem -> dItem.id }, contentType = {
            item.component.javaClass.simpleName
          }) { cItem ->
            HubBinder(navController, viewModel, cItem)
          }
        } else {
          item(span = {
            GridItemSpan(if (item.component.isGrid()) 1 else 2)
          }, key = item.id, contentType = {
            item.component.javaClass.simpleName
          }) {
            HubBinder(navController, viewModel, item)
          }
        }
      }
    }
  } else {
    Box(Modifier.fillMaxSize()) {
      CircularProgressIndicator(modifier = Modifier
        .align(Alignment.Center)
        .size(56.dp))
    }
  }
}

@HiltViewModel
class HubScreenViewModel @Inject constructor(
  private val spApiManager: SpApiManager,
  private val spPlayerServiceManager: SpPlayerServiceManager
): ViewModel(), HubScreenDelegate {
  private val _items = mutableStateOf<List<HubItem>?>(null)

  val items: List<HubItem>? get() = _items.value
  val isLoaded get() = !_items.value.isNullOrEmpty()

  suspend fun load(loader: suspend SpApiManager.() -> HubResponse) {
    _items.value = spApiManager.loader().body
  }

  override fun play(data: PlayFromContextData) {
    spPlayerServiceManager.playFromUri(data.uri)
  }
}