package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionEntry
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.SmallTopAppBar
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@Composable
fun YourLibraryContainerScreen(
  navController: NavController,
  viewModel: YourLibraryContainerViewModel = hiltViewModel()
) {
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    launch {
      viewModel.load()
    }
  }

  Scaffold(topBar = {
    Column {
      SmallTopAppBar(title = {
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
      }, contentPadding = PaddingValues(top = with(LocalDensity.current) { WindowInsets.statusBars.getTop(
        LocalDensity.current).toDp() }))
      AnimatedChipRow(
        listOf(ChipItem("playlists", "Playlists"), ChipItem("artists", "Artists"), ChipItem("albums", "Albums")),
        viewModel.selectedTabId
      ) { viewModel.selectedTabId = it }
    }
  }) { padding ->
    if (viewModel.content.isNotEmpty()) {
      LazyColumn(
        Modifier
          .padding(padding)
          .fillMaxSize()) {
        items(viewModel.content, key = { it.javaClass.simpleName + "_" + it.ceId() }) { item ->
          YlRenderer(item, modifier = Modifier.clickable {

          }.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).animateItemPlacement())
        }
      }
    } else {
      PagingLoadingPage(modifier = Modifier
        .padding(padding)
        .fillMaxSize())
    }
  }
}

class ChipItem (val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AnimatedChipRow(
  chips: List<ChipItem>,
  selectedId: String,
  onClick: (String) -> Unit,
) {
  LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
    items(chips.let {
      if (selectedId != "") it.filter { i -> i.id == selectedId } else it
    }, key = { it.id }) { item ->
      FilterChip(selected = selectedId == item.id, onClick = {
        onClick(if (selectedId == item.id) "" else item.id)
      }, label = {
        Text(item.name)
      }, selectedIcon = {
        Icon(Icons.Default.Check, null)
      }, modifier = Modifier.animateItemPlacement())
    }
  }
}

@HiltViewModel
class YourLibraryContainerViewModel @Inject constructor(
  private val dao: LocalCollectionDao
): ViewModel() {
  var selectedTabId: String by mutableStateOf("")
  var content = mutableStateListOf<CollectionEntry>()

  suspend fun load() {
    content.clear()

    val albums = dao.getAlbums()
    val artists = dao.getArtists()
    val pins = dao.getPins()
    val playlists = dao.getRootlist()

    content.addAll(
      (albums + artists + playlists).sortedByDescending { it.ceTimestamp() }.toMutableList().also { it.addAll(0, pins) }
    )
  }
}