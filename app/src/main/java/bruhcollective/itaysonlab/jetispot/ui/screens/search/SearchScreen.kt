package bruhcollective.itaysonlab.jetispot.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.proto.SearchEntity
import bruhcollective.itaysonlab.jetispot.proto.SearchViewResponse
import bruhcollective.itaysonlab.jetispot.ui.blocks.TwoColumnAndImageBlock
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.HubScreen
import bruhcollective.itaysonlab.jetispot.ui.shared.EmptyWindowInsets
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingInfoPage
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPagerApi::class
)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val virtualPagerState = rememberPagerState()
    val focusManager = LocalFocusManager.current
    val navController = LocalNavigationController.current

    val clearFunc: () -> Unit = {
        viewModel.launch {
            focusManager.clearFocus()
            virtualPagerState.scrollToPage(0)
            viewModel.clear()
        }
    }

    Scaffold(
        topBar = {
            Column(Modifier.statusBarsPadding()) {
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.searchQuery = it },
                    placeholder = {
                        if (viewModel.searchQuery.text.isEmpty()) {
                            Text(text = "What would you like to listen?")
                        }
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (viewModel.searchQuery.text.isNotEmpty()) {
                            IconButton(onClick = clearFunc) {
                                Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .focusTarget(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            8.dp
                        ), unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.launch {
                            focusManager.clearFocus()
                            virtualPagerState.scrollToPage(1)
                            viewModel.initiateSearch()
                        }
                    })
                )
            }
        }, contentWindowInsets = EmptyWindowInsets
    ) { padding ->
        HorizontalPager(
            count = 2,
            state = virtualPagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { idx ->
            when (idx) {
                0 -> HubScreen(loader = SpInternalApi::getBrowseView)
                1 -> SearchBinder(viewModel.searchResponse, onClick = { type, uri ->
                    when (type) {
                        SearchEntity.EntityCase.TRACK -> viewModel.dispatchPlay(uri)
                        else -> navController.navigate(uri)
                    }
                })
            }
        }
    }
}

@Composable
private fun SearchBinder(
    response: SearchViewResponse?,
    onClick: (SearchEntity.EntityCase, String) -> Unit
) {
    when {
        response?.hitsCount == 0 -> PagingInfoPage(title = "Nothing found", text = "Correct your request and try again", modifier = Modifier.fillMaxSize())
        response == null -> PagingLoadingPage(modifier = Modifier.fillMaxSize())
        else -> {
            LazyColumn(Modifier.fillMaxSize()) {
                items(response.hitsList) { entity ->
                    val text = remember(entity) {
                        when (entity.entityCase) {
                            SearchEntity.EntityCase.TRACK -> {
                                "Song • " + entity.track.trackArtistsList.joinToString { it.name }
                            }

                            SearchEntity.EntityCase.PLAYLIST -> {
                                when {
                                    entity.playlist.personalized -> "Playlist • Personalized for you"
                                    entity.playlist.ownedBySpotify -> "Playlist • By Spotify"
                                    else -> "Playlist"
                                }
                            }

                            SearchEntity.EntityCase.ALBUM -> {
                                "Album • " + entity.album.artistNamesList.joinToString()
                            }

                            SearchEntity.EntityCase.ARTIST -> {
                                "Artist"
                            }

                            else -> ""
                        }
                    }

                    TwoColumnAndImageBlock(
                        artworkUri = entity.imageUri,
                        title = entity.name,
                        text = text,
                        modifier = Modifier.clickable {
                            onClick(entity.entityCase, entity.uri)
                        }
                    )
                }
            }
        }
    }
}