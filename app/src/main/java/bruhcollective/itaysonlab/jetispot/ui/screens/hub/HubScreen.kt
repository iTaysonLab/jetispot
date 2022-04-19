package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpApiManager
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.core.objs.hub.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.core.objs.hub.isGrid
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun HubScreen(
    navController: NavController,
    needContentPadding: Boolean = true,
    loader: suspend SpApiManager.() -> HubResponse,
    viewModel: HubScreenViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    viewModel.needContentPadding = needContentPadding

    LaunchedEffect(Unit) {
        viewModel.load(loader)
    }

    when (viewModel.state) {
        is HubScreenViewModel.State.Loaded -> {
            LazyVerticalGrid(
                contentPadding = PaddingValues(if (needContentPadding) 16.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(if (needContentPadding) 8.dp else 0.dp),
                horizontalArrangement = Arrangement.spacedBy(if (needContentPadding) 8.dp else 0.dp),
                columns = GridCells.Fixed(2)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.statusBarsPadding())
                }

                (viewModel.state as HubScreenViewModel.State.Loaded).data.apply {
                    if (header != null) {
                        item(
                            key = header.id,
                            span = {
                                GridItemSpan(2)
                            },
                            contentType = header.component.javaClass.simpleName,
                        ) {
                            HubBinder(navController, viewModel, header)
                        }
                    }

                    body.forEach { item ->
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
            }
        }
        is HubScreenViewModel.State.Error -> {
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

        HubScreenViewModel.State.Loading -> {
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
class HubScreenViewModel @Inject constructor(
    private val spApiManager: SpApiManager,
    private val spPlayerServiceManager: SpPlayerServiceManager
) : ViewModel(), HubScreenDelegate {
    private val _state = mutableStateOf<State>(State.Loading)
    val state: State get() = _state.value

    // no state handle needed
    var needContentPadding: Boolean = false

    suspend fun load(loader: suspend SpApiManager.() -> HubResponse) {
        _state.value = try {
            State.Loaded(spApiManager.loader())
        } catch (e: Exception) {
            e.printStackTrace()
            State.Error(e)
        }
    }

    suspend fun reload(loader: suspend SpApiManager.() -> HubResponse) {
        _state.value = State.Loading
        load(loader)
    }

    override fun play(data: PlayFromContextData) {
        spPlayerServiceManager.playFromUri(data.uri)
    }

    override fun isSurroundedWithPadding() = needContentPadding

    sealed class State {
        class Loaded(val data: HubResponse) : State()
        class Error(val error: Exception) : State()
        object Loading : State()
    }
}