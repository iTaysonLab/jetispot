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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionContentFilter
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.screens.hub.CollectionViewModel
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import kotlin.math.max
import kotlin.math.roundToInt

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
    modifier = Modifier.statusBarsPadding(),
    title = { Text("Liked Songs") },
//    description = {
//      Text(
//        "${item.custom!!["count"]} songs",
//        fontSize = 12.sp,
//        color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
//      )
//    },
    scrollBehavior = scrollBehavior,
    navigationIcon = {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
      }
    },
    actions = {
      IconButton(
        onClick = { expandSortDropdown = !expandSortDropdown }
      ) {
        Icon(Icons.Rounded.Sort, null)
      }

      IconButton(
        onClick = { /*TODO*/ }
      ) {
        Icon(Icons.Rounded.Search, null)
      }
    },
//    contentPadding = PaddingValues(
//      top = with(LocalDensity.current) {
//        WindowInsets.statusBars.getTop(LocalDensity.current).toDp()
//      }
//    )
  )

  Column() {
    Row(Modifier.padding(horizontal = 16.dp)) {
      Box(Modifier.align(Alignment.CenterVertically)) {
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
    }

    val tags = item.custom?.get("cfr") as List<CollectionContentFilter>
    val currentTag = item.custom["cfr_cur"] as String

    val animHeight = animateFloatAsState(56 * (1f - scrollBehavior.state.collapsedFraction)).value
    LazyRow(
      modifier = Modifier
        .height(animHeight.dp)
        .padding(bottom = ((16 * (scrollBehavior.state.collapsedFraction)).dp)),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(tags) { item ->
        val selected = currentTag == item.name
        FilterChip(selected = selected, onClick = {
          delegate.sendCustomCommand(scope, if (selected) CollectionViewModel.Command.ClearTag else CollectionViewModel.Command.SetTag(item.query))
        }, label = {
          Text(item.name)
        }, leadingIcon = {
          if (selected) Icon(Icons.Rounded.Check, null)
        })
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LikedSongsTopAppBar(
  title: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable() (RowScope.() -> Unit) = {},
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  description: @Composable () -> Unit = { },
) {
  TwoRowsTopAppBar(
    title = title,
    description = description,
    titleTextStyle = MaterialTheme.typography.headlineMedium,
    smallTitleTextStyle = MaterialTheme.typography.titleLarge,
    titleBottomPadding = LargeTitleBottomPadding,
    smallTitle = title,
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    colors = colors,
    maxHeight = 152.dp,
    pinnedHeight = 64.dp,
    scrollBehavior = scrollBehavior,
    contentPadding = contentPadding,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TwoRowsTopAppBar(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit,
  titleTextStyle: TextStyle,
  titleBottomPadding: Dp,
  smallTitle: @Composable () -> Unit,
  smallTitleTextStyle: TextStyle,
  navigationIcon: @Composable () -> Unit,
  actions: @Composable() (RowScope.() -> Unit),
  colors: TopAppBarColors,
  maxHeight: Dp,
  pinnedHeight: Dp,
  scrollBehavior: TopAppBarScrollBehavior?,
  contentPadding: PaddingValues,
  description: @Composable () -> Unit,
) {
  if (maxHeight <= pinnedHeight) {
    throw IllegalArgumentException(
      "A TwoRowsTopAppBar max height should be greater than its pinned height"
    )
  }
  val pinnedHeightPx: Float
  val maxHeightPx: Float
  val titleBottomPaddingPx: Int
  LocalDensity.current.run {
    pinnedHeightPx = pinnedHeight.toPx()
    maxHeightPx = maxHeight.toPx()
    titleBottomPaddingPx = titleBottomPadding.roundToPx()
  }

  // Set a scroll offset limit that will hide just the title area and will keep the small title
  // area visible.
  SideEffect {
    if (scrollBehavior?.state?.heightOffsetLimit != pinnedHeightPx - maxHeightPx) {
      scrollBehavior?.state?.heightOffsetLimit = pinnedHeightPx - maxHeightPx
    }
  }

  val scrollPercentage =
    if (scrollBehavior == null || scrollBehavior.state.heightOffsetLimit == 0f) {
      0f
    } else {
      scrollBehavior.state.heightOffset / scrollBehavior.state.heightOffsetLimit
    }

  // Obtain the container Color from the TopAppBarColors.
  // This will potentially animate or interpolate a transition between the container color and the
  // container's scrolled color according to the app bar's scroll state.
  val scrollFraction = scrollBehavior?.state?.collapsedFraction ?: 0f
  val appBarContainerColor by colors.containerColor(scrollFraction)

  // Wrap the given actions in a Row.
  val actionsRow = @Composable {
    Row(
      horizontalArrangement = Arrangement.End,
      verticalAlignment = Alignment.CenterVertically,
      content = actions
    )
  }
  val titleAlpha = 1f - scrollPercentage
  // Hide the top row title semantics when its alpha value goes below 0.5 threshold.
  // Hide the bottom row title semantics when the top title semantics are active.
  val hideTopRowSemantics = scrollPercentage < 0.5f
  val hideBottomRowSemantics = !hideTopRowSemantics
  Surface(modifier = modifier, color = appBarContainerColor) {
    Column(
      modifier = Modifier.padding(contentPadding)
    ) {
      TopAppBarLayout(
        modifier = Modifier,
        heightPx = pinnedHeightPx,
        navigationIconContentColor =
        colors.navigationIconContentColor(scrollFraction).value,
        titleContentColor = colors.titleContentColor(scrollFraction).value,
        actionIconContentColor = colors.actionIconContentColor(scrollFraction).value,
        title = smallTitle,
        description = { },
        titleTextStyle = smallTitleTextStyle,
        titleAlpha = 1f - titleAlpha,
        titleVerticalArrangement = Arrangement.Center,
        titleHorizontalArrangement = Arrangement.Start,
        titleBottomPadding = 0,
        hideTitleSemantics = hideTopRowSemantics,
        navigationIcon = navigationIcon,
        actions = actionsRow,
      )
      TopAppBarLayout(
        modifier = Modifier.clipToBounds(),
        heightPx = maxHeightPx - pinnedHeightPx + (scrollBehavior?.state?.heightOffset ?: 0f),
        navigationIconContentColor =
        colors.navigationIconContentColor(scrollFraction).value,
        titleContentColor = colors.titleContentColor(scrollFraction).value,
        actionIconContentColor = colors.actionIconContentColor(scrollFraction).value,
        title = title,
        description = description,
        titleTextStyle = titleTextStyle,
        titleAlpha = titleAlpha,
        titleVerticalArrangement = Arrangement.Bottom,
        titleHorizontalArrangement = Arrangement.Start,
        titleBottomPadding = titleBottomPaddingPx,
        hideTitleSemantics = hideBottomRowSemantics,
        navigationIcon = {},
        actions = {}
      )
    }
  }
}

@Composable
private fun TopAppBarLayout(
  modifier: Modifier,
  heightPx: Float,
  navigationIconContentColor: Color,
  titleContentColor: Color,
  actionIconContentColor: Color,
  title: @Composable () -> Unit,
  titleTextStyle: TextStyle,
  titleAlpha: Float,
  titleVerticalArrangement: Arrangement.Vertical,
  titleHorizontalArrangement: Arrangement.Horizontal,
  titleBottomPadding: Int,
  hideTitleSemantics: Boolean,
  navigationIcon: @Composable () -> Unit,
  actions: @Composable () -> Unit,
  description: @Composable () -> Unit,
) {
  Layout(
    {
      Box(
        Modifier
          .layoutId("navigationIcon")
          .padding(start = TopAppBarHorizontalPadding)) {
        CompositionLocalProvider(
          LocalContentColor provides navigationIconContentColor,
          content = navigationIcon
        )
      }
      Box(
        Modifier
          .layoutId("title")
          .padding(horizontal = TopAppBarHorizontalPadding)
          .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics { } else Modifier)
      ) {
        ProvideTextStyle(value = titleTextStyle) {
          CompositionLocalProvider(
            LocalContentColor provides titleContentColor.copy(alpha = titleAlpha),
            content = title
          )
        }
      }
      Box(
        Modifier
          .layoutId("description")
          .padding(horizontal = TopAppBarHorizontalPadding + 1.dp)
          .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics { } else Modifier)
      ) {
        CompositionLocalProvider(
          LocalContentColor provides titleContentColor.copy(alpha = titleAlpha),
          content = description
        )
      }
      Box(
        Modifier
          .layoutId("actionIcons")
          .padding(end = TopAppBarHorizontalPadding)) {
        CompositionLocalProvider(
          LocalContentColor provides actionIconContentColor,
          content = actions
        )
      }
    },
    modifier = modifier
  ) { measurables, constraints ->
    val navigationIconPlaceable =
      measurables.first { it.layoutId == "navigationIcon" }
        .measure(constraints.copy(minWidth = 0))
    val actionIconsPlaceable =
      measurables.first { it.layoutId == "actionIcons" }
        .measure(constraints.copy(minWidth = 0))

    val maxTitleWidth = if (constraints.maxWidth == Constraints.Infinity) {
      constraints.maxWidth
    } else {
      (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width)
        .coerceAtLeast(0)
    }
    val titlePlaceable =
      measurables.first { it.layoutId == "title" }
        .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

    val descriptionPlaceable =
      measurables.first { it.layoutId == "description" }
        .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

    // Locate the title's baseline.
    val titleBaseline =
      if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
        titlePlaceable[LastBaseline]
      } else {
        0
      }

    val descriptionBaseline =
      if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
        titlePlaceable[LastBaseline]
      } else {
        0
      }

    val layoutHeight = heightPx.roundToInt()

    layout(constraints.maxWidth, layoutHeight) {
      // Navigation icon
      navigationIconPlaceable.placeRelative(
        x = 0,
        y = (layoutHeight - navigationIconPlaceable.height) / 2
      )

      // Title
      titlePlaceable.placeRelative(
        x = when (titleHorizontalArrangement) {
          Arrangement.Center -> (constraints.maxWidth - titlePlaceable.width) / 2
          Arrangement.End ->
            constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width
          // Arrangement.Start.
          // An TopAppBarTitleInset will make sure the title is offset in case the
          // navigation icon is missing.
          else -> max(TopAppBarTitleInset.roundToPx(), navigationIconPlaceable.width)
        },
        y = when (titleVerticalArrangement) {
          Arrangement.Center -> (layoutHeight - titlePlaceable.height) / 2
          // Apply bottom padding from the title's baseline only when the Arrangement is
          // "Bottom".
          Arrangement.Bottom ->
            if (titleBottomPadding == 0) layoutHeight - titlePlaceable.height
            else layoutHeight - titlePlaceable.height - max(
              0,
              titleBottomPadding - titlePlaceable.height + titleBaseline
            )
          // Arrangement.Top
          else -> 0
        }
      )

      descriptionPlaceable.placeRelative(
        x = when (titleHorizontalArrangement) {
          Arrangement.Center -> (constraints.maxWidth - descriptionPlaceable.width) / 2
          Arrangement.End ->
            constraints.maxWidth - descriptionPlaceable.width - actionIconsPlaceable.width
          // Arrangement.Start.
          // An TopAppBarTitleInset will make sure the title is offset in case the
          // navigation icon is missing.
          else -> max(TopAppBarTitleInset.roundToPx(), navigationIconPlaceable.width)
        },
        y = when (titleVerticalArrangement) {
          Arrangement.Center -> (layoutHeight - descriptionPlaceable.height) / 2
          // Apply bottom padding from the title's baseline only when the Arrangement is
          // "Bottom".
          Arrangement.Bottom ->
            if (titleBottomPadding == 0) layoutHeight - titlePlaceable.height
            else layoutHeight - titlePlaceable.height - max(
              titlePlaceable.height,
              titleBottomPadding - titlePlaceable.height + titleBaseline
            )
          // Arrangement.Top
          else -> 0
        }
      )

      // Action icons
      actionIconsPlaceable.placeRelative(
        x = constraints.maxWidth - actionIconsPlaceable.width,
        y = (layoutHeight - actionIconsPlaceable.height) / 2
      )
    }
  }
}

private val LargeTitleBottomPadding = 28.dp
private val TopAppBarHorizontalPadding = 4.dp

// A title inset when the App-Bar is a Medium or Large one. Also used to size a spacer when the
// navigation icon is missing.
private val TopAppBarTitleInset = 16.dp - TopAppBarHorizontalPadding