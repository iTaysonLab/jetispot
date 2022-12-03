package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionContentFilter
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.CollectionViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CollectionHeader(
  item: HubItem
) {
  val scope = rememberCoroutineScope()
  var expandSortDropdown by remember { mutableStateOf(false) }
  val delegate = LocalHubScreenDelegate.current

  Column(modifier = Modifier
    .fillMaxHeight()
    .background(
      brush = Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.primary, Color.Transparent)
      )
    )
    .padding(top = 64.dp)
    .statusBarsPadding()) {

    MediumText(text = stringResource(id = R.string.liked_songs), fontSize = 26.sp, modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(top = 8.dp))

    Subtext(text = "${item.custom!!["count"]}" + stringResource(id = R.string.songs), fontSize = 14.sp, modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(top = 2.dp))

    Row(
      Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 4.dp, bottom = 4.dp)) {
      IconButton(onClick = { /*TODO*/ },
        Modifier
          .offset(y = 2.dp)
          .align(Alignment.CenterVertically)
          .size(28.dp)) {
        Icon(Icons.Rounded.AddCircle, null)
      }

      Spacer(Modifier.width(20.dp))

      IconButton(onClick = { /*TODO*/ },
        Modifier
          .offset(y = 2.dp)
          .align(Alignment.CenterVertically)
          .size(28.dp)) {
        Icon(Icons.Rounded.Search, null)
      }

      Spacer(Modifier.width(20.dp))

      Box(
        Modifier
          .offset(y = 2.dp)
          .align(Alignment.CenterVertically)) {
        IconButton(onClick = {
          expandSortDropdown = !expandSortDropdown
        },
          Modifier
            .size(28.dp)) {
          Icon(Icons.Rounded.Sort, null)
        }

        DropdownMenu(expanded = expandSortDropdown, offset = DpOffset(4.dp, 4.dp), onDismissRequest = { expandSortDropdown = false }) {
          Subtext(text = stringResource(id = R.string.sort), modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp))

          val sel = delegate.sendCustomCommand(scope, CollectionViewModel.Command.GetSort) as LocalCollectionDao.TrackSorts

          DropdownMenuItem(text = {
            Text(stringResource(id = R.string.sort_time))
          }, onClick = {
            delegate.sendCustomCommand(scope, CollectionViewModel.Command.SetSort(LocalCollectionDao.TrackSorts.ByTime))
          }, trailingIcon = {
            if (sel == LocalCollectionDao.TrackSorts.ByTime) {
              Icon(Icons.Rounded.Check, null, modifier = Modifier.padding(start = 12.dp))
            }
          })

          DropdownMenuItem(text = {
            Text(stringResource(id = R.string.sort_title))
          }, onClick = {
            delegate.sendCustomCommand(scope, CollectionViewModel.Command.SetSort(LocalCollectionDao.TrackSorts.ByName))
          }, trailingIcon = {
            if (sel == LocalCollectionDao.TrackSorts.ByName) {
              Icon(Icons.Rounded.Check, null, modifier = Modifier.padding(start = 12.dp))
            }
          })

          DropdownMenuItem(text = {
            Text(stringResource(id = R.string.sort_artist))
          }, onClick = {
            delegate.sendCustomCommand(scope, CollectionViewModel.Command.SetSort(LocalCollectionDao.TrackSorts.ByArtist))
          }, trailingIcon = {
            if (sel == LocalCollectionDao.TrackSorts.ByArtist) {
              Icon(Icons.Rounded.Check, null, modifier = Modifier.padding(start = 12.dp))
            }
          })

          DropdownMenuItem(text = {
            Text(stringResource(id = R.string.sort_album))
          }, onClick = {
            delegate.sendCustomCommand(scope, CollectionViewModel.Command.SetSort(LocalCollectionDao.TrackSorts.ByAlbum))
          }, trailingIcon = {
            if (sel == LocalCollectionDao.TrackSorts.ByAlbum) {
              Icon(Icons.Rounded.Check, null, modifier = Modifier.padding(start = 12.dp))
            }
          })

          Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

          DropdownMenuItem(text = {
            Text(stringResource(id = R.string.sort_invert))
          }, onClick = {
            delegate.sendCustomCommand(scope, CollectionViewModel.Command.ToggleSortInvert)
          }, trailingIcon = {
            Checkbox(checked = delegate.sendCustomCommand(scope, CollectionViewModel.Command.GetSortInvert) as Boolean, onCheckedChange = {}, Modifier.offset(x = 10.dp))
          })
        }
      }

      Spacer(Modifier.weight(1f))

      Box(Modifier.size(48.dp)) {
        Box(
          Modifier
            .clip(CircleShape)
            .size(48.dp)
            .background(MaterialTheme.colorScheme.primary)
            .clickable {

            }
        ) {
          Icon(
            imageVector = Icons.Rounded.PlayArrow,
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = null,
            modifier = Modifier
              .size(32.dp)
              .align(Alignment.Center)
          )
        }

        Box(
          Modifier
            .align(Alignment.BottomEnd)
            .offset(4.dp, 4.dp)
            .clip(CircleShape)
            .size(22.dp)
            .background(MaterialTheme.colorScheme.compositeSurfaceElevation(4.dp))
        ) {
          Icon(
            imageVector = Icons.Rounded.Shuffle,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
            modifier = Modifier
              .padding(4.dp)
              .align(Alignment.Center)
          )
        }
      }
    }

    val tags = item.custom["cfr"] as List<CollectionContentFilter>
    val currentTag = item.custom["cfr_cur"] as String

    LazyRow(modifier = Modifier.padding(top = 4.dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(tags) { item ->
        val selected = currentTag == item.query
        FilterChip(selected = selected, onClick = {
          delegate.sendCustomCommand(scope, if (selected) CollectionViewModel.Command.ClearTag else CollectionViewModel.Command.SetTag(item.query))
        }, label = {
          Text(item.name)
        }, leadingIcon = {
          if (selected) Icon(Icons.Rounded.Check, null)
        }, modifier = Modifier.animateItemPlacement())
      }
    }
  }
}