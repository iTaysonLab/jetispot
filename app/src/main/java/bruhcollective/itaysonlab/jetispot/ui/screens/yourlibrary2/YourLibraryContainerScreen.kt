package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionEntry
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.PredefCeType
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.AppPreferences
import bruhcollective.itaysonlab.jetispot.ui.shared.AppPreferences.UseGrid
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.SmallTopAppBar
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
  val columnState = rememberLazyListState()
  val gridState = rememberLazyGridState()
  LaunchedEffect(Unit) {
    launch {
      viewModel.load()
    }
  }

  Scaffold(
    topBar = {
      Column {
        SmallTopAppBar(
          title = { Text("Your Library") },
          navigationIcon = {
            IconButton(onClick = { /* TODO */ }) {
              Icon(Icons.Rounded.AccountCircle, null)
            }
          },
          actions = {
            IconButton(onClick = { /* TODO */ }) {
              Icon(Icons.Rounded.Search, null)
            }
          },
          contentPadding = PaddingValues(
            top = with(LocalDensity.current) {
              WindowInsets.statusBars.getTop(LocalDensity.current).toDp()
            }
          )
        )

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
              if (UseGrid!!) gridState.animateScrollToItem(0) else columnState.animateScrollToItem(0)
            }
          }
        }
        val Grid = remember { mutableStateOf(false) }
        Grid.value = UseGrid!!
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.End){
          IconToggleButton(
              checked = Grid.value,
            onCheckedChange = {
              Grid.value = it
              UseGrid = it
              scope.launch {
                  viewModel.content = emptyList()
                  viewModel.load()
                  if (UseGrid!!) gridState.animateScrollToItem(0) else columnState.animateScrollToItem(0)
              }
            }
          ) {
            Icon(if (Grid.value) Icons.Rounded.ViewList else Icons.Rounded.Apps, null, tint = LocalContentColor.current.copy(alpha = LocalContentAlpha.current))
          }
        }
      }
    }
  ) { padding ->
    if (viewModel.content.isNotEmpty()) {
      if (UseGrid!!) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          state = gridState,
          modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(12.dp)
        ) {
          items(
            viewModel.content,
            key = { it.javaClass.simpleName + "_" + it.ceId() },
            contentType = { it.javaClass.simpleName }) { item ->
            YLCardRender(
              item,
              modifier = Modifier
                .width(172.dp)
                .clickable { navController.navigate(item.ceUri()) }
                .padding(bottom = 12.dp)
            )
          }
        }
      } else {
        LazyColumn(
          state = columnState,
          modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                .animateItemPlacement()
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
            )
          }
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