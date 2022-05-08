package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionRepository
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtist
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionModel
import bruhcollective.itaysonlab.jetispot.ui.screens.dac.DacViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingErrorPage
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun YourLibraryContainerScreen(
  navController: NavController,
  viewModel: YourLibraryContainerViewModel = viewModel()
) {
  val scope = rememberCoroutineScope()

  val pagerState = rememberPagerState()
  val currentTabIndex = pagerState.currentPage

  Scaffold(topBar = {
    Column {
      bruhcollective.itaysonlab.jetispot.ui.shared.evo.SmallTopAppBar(title = {
        Text("Your Library")
      }, navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
          Icon(Icons.Default.AccountCircle, null)
        }
      }, actions = {
        IconButton(onClick = { navController.popBackStack() }) {
          Icon(Icons.Default.Search, null)
        }

        IconButton(onClick = { navController.navigate("library/debug") }) {
          Icon(Icons.Default.BugReport, null)
        }
      }, contentPadding = PaddingValues(top = with(LocalDensity.current) { WindowInsets.statusBars.getTop(LocalDensity.current).toDp() }))
      TabRow(selectedTabIndex = currentTabIndex, indicator = { tabPositions ->
        TabRowDefaults.Indicator(
          Modifier
            .tabIndicatorOffset(tabPositions[currentTabIndex])
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )
      }, divider = {}) {
        viewModel.sources.forEachIndexed { index, item ->
          Tab(selected = currentTabIndex == index,
            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
            text = { Text(text = item.title) })
        }
      }
    }
  }) { padding ->
    HorizontalPager( // 4.
      count = viewModel.sources.size,
      state = pagerState,
    ) { tabIndex ->
      YourLibraryRenderer(navController, viewModel.sources[tabIndex])
    }
  }
}

class YourLibraryContainerViewModel: ViewModel() {
  val sources = listOf(YourLibrarySource.Default, YourLibrarySource.Albums, YourLibrarySource.Artists)
}

@SuppressLint("ComposableNaming")
sealed class YourLibrarySource <T: CollectionModel> (
  val title: String
) {
  private val contentState = mutableStateOf<State>(State.Loading)

  suspend fun load(repository: SpCollectionManager) {
    contentState.value = try {
      State.Loaded(getData(repository))
    } catch (e: Exception) {
      State.Error(e)
    }
  }

  @Composable
  fun render(navController: NavController) {
    when (val state = contentState.value) {
      is State.Loaded<*> -> {
        LazyColumn {
          items(state.data) { item ->
            render(navController, item as T)
          }
        }
      }

      is State.Error -> {
        PagingErrorPage(onReload = { }, modifier = Modifier.fillMaxSize())
      }

      State.Loading -> {
        PagingLoadingPage(Modifier.fillMaxSize())
      }
    }
  }

  @Composable abstract fun render(navController: NavController, item: T)
  abstract suspend fun getData(repository: SpCollectionManager): List<T>

  sealed class State {
    class Loaded <T: CollectionModel> (val data: List<T>) : State()
    class Error(val error: Exception) : State()
    object Loading : State()
  }

  object Default: YourLibrarySource<CollectionAlbum>("All") {
    @Composable
    override fun render(navController: NavController, item: CollectionAlbum) {

    }

    override suspend fun getData(repository: SpCollectionManager) = listOf<CollectionAlbum>()
  }

  object Albums: YourLibrarySource<CollectionAlbum>("Albums") {
    @Composable
    override fun render(navController: NavController, item: CollectionAlbum) {
      Row(
        Modifier
          .clickable {
            navController.navigate(item.uri)
          }
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)) {
        AsyncImage(model = "https://i.scdn.co/image/${item.picture}", contentDescription = null, modifier = Modifier
          .size(64.dp))

        Column(Modifier.padding(start = 16.dp).align(Alignment.CenterVertically)) {
          Text(text = item.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
          Text(text = item.rawArtistsData.split("|").joinToString { it.split("=")[1] }, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
        }
      }
    }

    override suspend fun getData(repository: SpCollectionManager) = repository.albums()
  }

  object Artists: YourLibrarySource<CollectionArtist>("Artists") {
    @Composable
    override fun render(navController: NavController, item: CollectionArtist) {
      Row(
        Modifier
          .clickable {
            navController.navigate(item.uri)
          }
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)) {
        AsyncImage(model = "https://i.scdn.co/image/${item.picture}", contentDescription = null, modifier = Modifier
          .size(56.dp)
          .clip(
            CircleShape
          ))
        Text(text = item.name, modifier = Modifier
          .padding(start = 16.dp)
          .align(Alignment.CenterVertically))
      }
    }

    override suspend fun getData(repository: SpCollectionManager) = repository.artists()
  }
}