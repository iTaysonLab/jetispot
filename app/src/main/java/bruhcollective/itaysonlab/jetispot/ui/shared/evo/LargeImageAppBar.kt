package bruhcollective.itaysonlab.jetispot.ui.shared.evo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.roundToInt


@Composable
fun LargeImageTopAppBar(
  title: @Composable () -> Unit,
  artwork: @Composable () -> Unit,
  artist: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable() (RowScope.() -> Unit) = {},
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  maxHeight: Dp = 256.dp,
  smallTitle: @Composable () -> Unit
) {
  TwoRowsTopAppBar(
    title = title,
    artwork = artwork,
    artist = artist,
    titleTextStyle = MaterialTheme.typography.headlineMedium,
    smallTitleTextStyle = MaterialTheme.typography.titleLarge.plus(TextStyle(lineHeight = 20.sp)),
    titleBottomPadding = LargeTitleBottomPadding,
    smallTitle = smallTitle,
    modifier = modifier,
    navigationIcon = navigationIcon,
    actions = actions,
    colors = colors,
    maxHeight = maxHeight,
    pinnedHeight = 64.dp,
    scrollBehavior = scrollBehavior,
    contentPadding = contentPadding,
  )
}

@Composable
private fun TwoRowsTopAppBar(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit,
  artwork: @Composable () -> Unit,
  artist: @Composable () -> Unit,
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
  contentPadding: PaddingValues
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
    if (scrollBehavior?.state?.offsetLimit != pinnedHeightPx - maxHeightPx) {
      scrollBehavior?.state?.offsetLimit = pinnedHeightPx - maxHeightPx
    }
  }

  val scrollPercentage =
    if (scrollBehavior == null || scrollBehavior.state.offsetLimit == 0f) {
      0f
    } else {
      scrollBehavior.state.offset / scrollBehavior.state.offsetLimit
    }

  // Obtain the container Color from the TopAppBarColors.
  // This will potentially animate or interpolate a transition between the container color and the
  // container's scrolled color according to the app bar's scroll state.
  val scrollFraction = scrollBehavior?.scrollFraction ?: 0f
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
        navigationIconContentColor = colors.navigationIconContentColor(scrollFraction).value,
        titleContentColor = colors.titleContentColor(scrollFraction).value,
        actionIconContentColor = colors.actionIconContentColor(scrollFraction).value,
        title = smallTitle,
        artwork = artwork,
        artworkSize = 48.dp,
        artist = { },
        titleTextStyle = smallTitleTextStyle,
        scrollPercentage = 1f - titleAlpha,
        titleVerticalArrangement = Arrangement.Center,
        titleHorizontalArrangement = Arrangement.Start,
        artworkVerticalArrangement = Arrangement.Center,
        artworkHorizontalArrangement = Arrangement.End,
        titleBottomPadding = 0,
        hideTitleSemantics = hideTopRowSemantics,
        navigationIcon = navigationIcon,
        actions = actionsRow
      )
      TopAppBarLayout(
        modifier = Modifier.clipToBounds(),
        heightPx = maxHeightPx - pinnedHeightPx + (scrollBehavior?.state?.offset ?: 0f),
        navigationIconContentColor = colors.navigationIconContentColor(scrollFraction).value,
        titleContentColor = colors.titleContentColor(scrollFraction).value,
        actionIconContentColor = colors.actionIconContentColor(scrollFraction).value,
        title = title,
        artwork = artwork,
        artworkSize = 164.dp,
        artist = artist,
        titleTextStyle = titleTextStyle,
        scrollPercentage = titleAlpha,
        titleVerticalArrangement = Arrangement.Bottom,
        titleHorizontalArrangement = Arrangement.Start,
        artworkVerticalArrangement = Arrangement.Bottom,
        artworkHorizontalArrangement = Arrangement.End,
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
  artwork: @Composable () -> Unit,
  artworkSize: Dp,
  artist: @Composable () -> Unit,
  titleTextStyle: TextStyle,
  scrollPercentage: Float,
  titleVerticalArrangement: Arrangement.Vertical,
  titleHorizontalArrangement: Arrangement.Horizontal,
  artworkVerticalArrangement: Arrangement.Vertical,
  artworkHorizontalArrangement: Arrangement.Horizontal,
  titleBottomPadding: Int,
  hideTitleSemantics: Boolean,
  navigationIcon: @Composable () -> Unit,
  actions: @Composable () -> Unit
) {
  val artworkSmall = artworkVerticalArrangement == Arrangement.Center

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
          .layoutId("artist")
          .padding(end = TopAppBarHorizontalPadding)
          .alpha(scrollPercentage)
      ) {
        CompositionLocalProvider(
          LocalContentColor provides actionIconContentColor,
          content = artist
        )
      }
      Box(
        Modifier
          .layoutId("title")
          .padding(horizontal = TopAppBarHorizontalPadding)
          .alpha(scrollPercentage)
          .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics { } else Modifier)
      ) {
        ProvideTextStyle(value = titleTextStyle) {
          CompositionLocalProvider(
            content = title
          )
        }
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
      Box(
        Modifier
          .layoutId("artwork")
          .size(artworkSize)
          .alpha(scrollPercentage)
          .clip(RoundedCornerShape(if (artworkSmall) 4.dp else 8.dp))
      ) {
        CompositionLocalProvider(
          LocalContentColor provides actionIconContentColor,
          content = artwork
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

    val artworkPlaceable =
      measurables.first { it.layoutId == "artwork" }
        .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

    val artistPlaceable =
      measurables.first { it.layoutId == "artist" }
        .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

    // Locate the title's baseline.
    val titleBaseline =
      if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
        titlePlaceable[LastBaseline]
      } else {
        0
      }

    val artworkBaseline =
      if (artworkPlaceable[LastBaseline] != AlignmentLine.Unspecified) {
        artworkPlaceable[LastBaseline]
      } else {
        0
      }

    val artistBaseline =
      if (artistPlaceable[LastBaseline] != AlignmentLine.Unspecified) {
        artistPlaceable[LastBaseline]
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

      titlePlaceable.placeRelative(
        x = when (titleHorizontalArrangement) {
          Arrangement.Center -> (constraints.maxWidth - titlePlaceable.width) / 2
          Arrangement.End ->
            constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width
          // Arrangement.Start.
          // An TopAppBarTitleInset will make sure the title is offset in case the
          // navigation icon is missing.
          else -> max(
            TopAppBarTitleInset.roundToPx(),
            navigationIconPlaceable.width
          )
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

      artworkPlaceable.placeRelative(
        x = if (artworkSmall)
          (constraints.maxWidth - artworkPlaceable.width - actionIconsPlaceable.width) - 4
        else
          (constraints.maxWidth - artworkPlaceable.width - actionIconsPlaceable.width) - 42,
        y = when (artworkVerticalArrangement) {
          Arrangement.Center -> 28
          // Apply bottom padding from the title's baseline only when the Arrangement is
          // "Bottom".
          Arrangement.Bottom ->
            if (titleBottomPadding == 0) layoutHeight - artworkPlaceable.height
            else layoutHeight - artworkPlaceable.height - max(
              94,
              titleBottomPadding - artworkPlaceable.height + artworkBaseline
            )
          // Arrangement.Top
          else -> 0
        }
      )

      artistPlaceable.placeRelative(
        x = when (titleHorizontalArrangement) {
          Arrangement.Center -> (constraints.maxWidth - artistPlaceable.width) / 2
          Arrangement.End ->
            constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width
          // Arrangement.Start.
          // An TopAppBarTitleInset will make sure the title is offset in case the
          // navigation icon is missing.
          else -> max(
            TopAppBarTitleInset.roundToPx(),
            navigationIconPlaceable.width
          )
        },
        y = when (titleVerticalArrangement) {
          Arrangement.Center -> (layoutHeight - titlePlaceable.height) / 2
          // Apply bottom padding from the title's baseline only when the Arrangement is
          // "Bottom".
          Arrangement.Bottom ->
            if (titleBottomPadding == 0) layoutHeight - titlePlaceable.height
            else layoutHeight - titlePlaceable.height - max(
              248,
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