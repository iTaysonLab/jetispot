package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionEntry
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.PredefCeType
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalFoundationApi::class
)
@Composable
fun YourLibraryContainerScreen(
  viewModel: YourLibraryContainerViewModel = hiltViewModel()
) {
  val navController = LocalNavigationController.current
  val scope = rememberCoroutineScope()
  val state = rememberLazyListState()

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
        IconButton(onClick = { /* TODO */ }) {
          Icon(Icons.Rounded.AccountCircle, null)
        }
      }, actions = {
        IconButton(onClick = { /* TODO */ }) {
          Icon(Icons.Rounded.Search, null)
        }
      })
      AnimatedChipRow(
        listOf(
          ChipItem("playlists", stringResource(id = R.string.filter_playlist)),
          ChipItem("artists", stringResource(id = R.string.filter_artist)),
          ChipItem("albums", stringResource(id = R.string.filter_album)),
          ChipItem("shows", stringResource(id = R.string.filter_show))
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
  }) { padding ->
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
          YlRenderer(item, modifier = Modifier
            .clickable { navController.navigate(item.ceUri()) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateItemPlacement())
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
    }, key = { it.id }) { item ->
      val selected = selectedId == item.id
      FilterChip(selected = selected, onClick = {
        onClick(if (selected) "" else item.id)
      }, label = {
        Text(item.name)
      }, leadingIcon = {
        if (selected) Icon(Icons.Rounded.Check, null)
      }, modifier = Modifier.animateItemPlacement())
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
      "shows" -> FetchType.Shows
      else -> FetchType.All
    }

    val albums = dao.getAlbums()
    val artists = dao.getArtists()
    val playlists = dao.getRootlist()
    val shows = dao.getShows()

    val pins = dao.getPins().filter { p ->
      when (type) {
        FetchType.Playlists -> p.uri.contains("playlist")
        FetchType.Artists -> p.uri.contains("artist")
        FetchType.Albums -> p.uri.contains("album")
        FetchType.Shows -> p.uri.contains("show")
        FetchType.All -> true
      }
    }

    content = (when (type) {
      FetchType.Playlists -> playlists
      FetchType.Artists -> artists
      FetchType.Albums -> albums
      FetchType.Shows -> shows
      FetchType.All -> {
        (albums + artists + playlists + shows).sortedByDescending { it.ceTimestamp() }
      }
    }.toMutableList().also {
      it.addAll(0, pins)
      it.filter { p -> p.ceUri().startsWith("spotify:collection") }.forEach { pF ->
        when (pF.ceUri()) {
          "spotify:collection" -> pF.ceModifyPredef(PredefCeType.COLLECTION, dao.trackCount().toString())
          "spotify:collection:podcasts:episodes" -> pF.ceModifyPredef(PredefCeType.EPISODES, "")
        }
      }
    })
  }

  enum class FetchType {
    All,
    Playlists,
    Artists,
    Albums,
    Shows
  }
}