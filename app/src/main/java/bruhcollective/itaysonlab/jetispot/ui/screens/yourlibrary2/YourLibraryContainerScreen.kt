package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionEntry
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.PredefCeType
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.LargeTopAppBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun YourLibraryContainerScreen(
  navController: LambdaNavigationController,
  viewModel: YourLibraryContainerViewModel = hiltViewModel()
) {
  val scope = rememberCoroutineScope()
  val state = rememberLazyListState()
  val scrollBehavior = rememberEUCScrollBehavior()

  LaunchedEffect(Unit) {
    launch {
      viewModel.load()
    }
  }

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      Column {
        LargeTopAppBar(
          title = { Text("Your Library") },
          navigationIcon = {
            IconButton(onClick = { /* TODO */ }) {
              Icon(
                Icons.Rounded.AccountCircle,
                null,
                modifier = Modifier
                  .size(32.dp)
                  .padding(top = 2.dp)
              )
            }
          },
          actions = {
            IconButton(onClick = { /* TODO */ }) {
              Icon(Icons.Rounded.Search, null)
            }
          },
          scrollBehavior = scrollBehavior,
          contentPadding = PaddingValues(
            top = with(LocalDensity.current) {
              WindowInsets.statusBars.getTop(LocalDensity.current).toDp()
            }
          )
        )

        val animatedHeight = animateFloatAsState(44 * (1f - scrollBehavior.scrollFraction))
        Box(Modifier.height(animatedHeight.value.dp)) {
          AnimatedChipRow(
            listOf(
              ChipItem("playlists", "Playlists"),
              ChipItem("artists", "Artists"),
              ChipItem("albums", "Albums")
            ),
            viewModel.selectedTabId
          ) {
            viewModel.selectedTabId = it
            scope.launch {
              viewModel.load()
              if (viewModel.selectedTabId == "") {
                delay(25L)
                state.animateScrollToItem(0)
              }
            }
          }
        }
      }
    }
  ) { padding ->
    if (viewModel.content.isNotEmpty()) {
      LazyColumn(
        state = state,
        modifier = Modifier
          .padding(padding)
          .fillMaxSize()
      ) {
        items(
          viewModel.content,
          key = { it.javaClass.simpleName + "_" + it.ceId() },
          contentType = { it.javaClass.simpleName }) { item ->
          YlRenderer(
            item,
            modifier = Modifier
              .clickable { navController.navigate(item.ceUri()) }
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp)
              .animateItemPlacement()
          )
        }
      }
    } else {
      PagingLoadingPage(
        modifier = Modifier
          .padding(padding)
          .fillMaxSize()
      )
    }
  }
}

class ChipItem(val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AnimatedChipRow(
  chips: List<ChipItem>,
  selectedId: String,
  onClick: (String) -> Unit,
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(chips.let {
      if (selectedId != "") it.filter { i -> i.id == selectedId } else it
    },
      key = { it.id }) { item ->
      FilterChip(
        selected = selectedId == item.id,
        onClick = { onClick(if (selectedId == item.id) "" else item.id) },
        label = { Text(item.name) },
        selectedIcon = { Icon(Icons.Rounded.Check, null) },
        modifier = Modifier.animateItemPlacement()
      )
    }
  }
}

@HiltViewModel
class YourLibraryContainerViewModel @Inject constructor(
  private val dao: LocalCollectionDao
) : ViewModel() {
  var selectedTabId: String by mutableStateOf("")
  var content by mutableStateOf<List<CollectionEntry>>(emptyList())

  suspend fun load() {
    val type = when (selectedTabId) {
      "playlists" -> FetchType.Playlists
      "albums" -> FetchType.Albums
      "artists" -> FetchType.Artists
      else -> FetchType.All
    }

    val albums = dao.getAlbums()
    val artists = dao.getArtists()
    val playlists = dao.getRootlist()
    val pins = dao.getPins().filter { p ->
      when (type) {
        FetchType.Playlists -> p.uri.contains("playlist")
        FetchType.Artists -> p.uri.contains("artist")
        FetchType.Albums -> p.uri.contains("album")
        FetchType.All -> true
      }
    }

    content = (when (type) {
      FetchType.Playlists -> playlists
      FetchType.Artists -> artists
      FetchType.Albums -> albums
      FetchType.All -> {
        (albums + artists + playlists).sortedByDescending { it.ceTimestamp() }
      }
    }.toMutableList()
      .also {
        it.addAll(0, pins)
        it.filter { p -> p.ceUri().startsWith("spotify:collection") }.forEach { pF ->
          when (pF.ceUri()) {
            "spotify:collection" -> pF.ceModifyPredef(
              PredefCeType.COLLECTION,
              dao.trackCount().toString()
            )
            "spotify:collection:podcasts:episodes" -> pF.ceModifyPredef(PredefCeType.EPISODES, "")
          }
        }
      }
    )
  }

  enum class FetchType {
    All,
    Playlists,
    Artists,
    Albums
  }
}