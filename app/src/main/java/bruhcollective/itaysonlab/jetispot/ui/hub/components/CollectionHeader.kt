package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionContentFilter
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.CollectionViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionHeader(
  item: HubItem,
  scrollBehavior: TopAppBarScrollBehavior
) {
  val navController = LocalNavigationController.current
  val scope = rememberCoroutineScope()
  var expandSortDropdown by remember { mutableStateOf(false) }
  val delegate = LocalHubScreenDelegate.current

  LargeTopAppBar(
    title = { Text("Liked Songs") },
    scrollBehavior = scrollBehavior,
    navigationIcon = {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
      }
    },
    actions = {
      IconButton(onClick = { expandSortDropdown = !expandSortDropdown }) {
        Icon(Icons.Rounded.Sort, null)
      }

      IconButton(
        onClick = { /*TODO*/ }
      ) {
        Icon(Icons.Rounded.Search, null)
      }
    },
  )

  Column {
    Row(Modifier.padding(horizontal = 16.dp)) {
      Box(Modifier.align(Alignment.CenterVertically)) {
        DropdownMenu(
          expanded = expandSortDropdown,
          offset = DpOffset(4.dp, 4.dp),
          onDismissRequest = { expandSortDropdown = false }
        ) {
          Subtext(
            text = stringResource(id = R.string.sort),
            modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
          )

          val sel = delegate.sendCustomCommand(
            scope,
            CollectionViewModel.Command.GetSort
          ) as LocalCollectionDao.TrackSorts

          DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_time)) },
            onClick = {
              delegate.sendCustomCommand(
                scope,
                CollectionViewModel.Command.SetSort(LocalCollectionDao.TrackSorts.ByTime)
              )
            },
            trailingIcon = {
              if (sel == LocalCollectionDao.TrackSorts.ByTime) {
                Icon(
                  Icons.Rounded.Check,
                  null,
                  modifier = Modifier.padding(start = 12.dp)
                )
              }
            }
          )

          DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_title)) },
            onClick = {
              delegate.sendCustomCommand(
                scope,
                CollectionViewModel.Command.SetSort(LocalCollectionDao.TrackSorts.ByName))
            },
            trailingIcon = {
              if (sel == LocalCollectionDao.TrackSorts.ByName) {
                Icon(
                  Icons.Rounded.Check,
                  null,
                  modifier = Modifier.padding(start = 12.dp)
                )
              }
            }
          )

          DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_artist)) },
            onClick = {
              delegate.sendCustomCommand(
                scope,
                CollectionViewModel.Command.SetSort(LocalCollectionDao.TrackSorts.ByArtist)
              )
            },
            trailingIcon = {
              if (sel == LocalCollectionDao.TrackSorts.ByArtist) {
                Icon(
                  Icons.Rounded.Check,
                  null,
                  modifier = Modifier.padding(start = 12.dp)
                )
              }
            }
          )

          DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_album)) },
            onClick = {
              delegate.sendCustomCommand(
                scope,
                CollectionViewModel.Command.SetSort(LocalCollectionDao.TrackSorts.ByAlbum))
            },
            trailingIcon = {
              if (sel == LocalCollectionDao.TrackSorts.ByAlbum) {
                Icon(
                  Icons.Rounded.Check,
                  null,
                  modifier = Modifier.padding(start = 12.dp)
                )
              }
            }
          )

          Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            modifier = Modifier.padding(vertical = 4.dp)
          )

          DropdownMenuItem(
            text = { Text(stringResource(id = R.string.sort_invert)) },
            onClick = {
              delegate.sendCustomCommand(scope, CollectionViewModel.Command.ToggleSortInvert)
            },
            trailingIcon = {
              Checkbox(
                checked = delegate.sendCustomCommand(
                  scope,
                  CollectionViewModel.Command.GetSortInvert
                ) as Boolean,
                onCheckedChange = {},
                Modifier.offset(x = 10.dp)
              )
            }
          )
        }
      }
    }

    val tags = item.custom?.get("cfr") as List<CollectionContentFilter>
    val currentTag = item.custom["cfr_cur"] as String

    LazyRow(
      modifier = Modifier
        .height(animateFloatAsState(32 * (1f - scrollBehavior.state.collapsedFraction)).value.dp)
        .padding(bottom = 0.dp),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(tags) { item ->
        val selected = currentTag == item.name
        FilterChip(
          selected = selected,
          onClick = {
            delegate.sendCustomCommand(
              scope,
              if (selected)
                CollectionViewModel.Command.ClearTag
              else
                CollectionViewModel.Command.SetTag(item.query)
            )
          },
          label = { Text(item.name) },
          leadingIcon = { if (selected) Icon(Icons.Rounded.Check, null) }
        )
      }
    }
  }
}