package bruhcollective.itaysonlab.jetispot.ui.shared.evo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageTopAppBar(
  title: @Composable () -> Unit,
  artwork: @Composable () -> Unit,
  description: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable() (RowScope.() -> Unit) = {},
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  maxHeight: Dp = 256.dp,
  smallTitle: @Composable () -> Unit = {}
) {
  TwoRowsTopAppBar(
    title = title,
    artwork = artwork,
    description = description,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TwoRowsTopAppBar(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit,
  artwork: @Composable () -> Unit,
  description: @Composable () -> Unit,
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
  val appBarContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp).copy(scrollFraction)

  // Wrap the given actions in a Row.
  val actionsRow = @Composable {
    Row(
      horizontalArrangement = Arrangement.End,
      verticalAlignment = Alignment.CenterVertically,
      content = actions
    )
  }
  val colors = MaterialTheme.colorScheme
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
        navigationIconContentColor = colors.onBackground,
        titleContentColor = colors.onBackground.copy(scrollFraction),
        actionIconContentColor = colors.onSurfaceVariant,
        title = smallTitle,
        artwork = {
          Column(Modifier.size(pinnedHeight), Arrangement.Center) {
            Box(modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(4.dp))) {
              CompositionLocalProvider(content = artwork)
            }
          }
        },
        artworkSize = pinnedHeight,
        description = {},
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
        heightPx = maxHeightPx - pinnedHeightPx + (scrollBehavior?.state?.heightOffset ?: 0f),
        navigationIconContentColor = Color.Transparent,
        titleContentColor = colors.onBackground.copy(scrollFraction),
        actionIconContentColor = Color.Transparent,
        title = title,
        artwork = {
          Column(
            Modifier
              .size(8.dp + maxHeight - pinnedHeight - WindowInsets.statusBars.asPaddingValues(LocalDensity.current).calculateTopPadding()),
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
            ) {
              CompositionLocalProvider(content = artwork)
            }
          }
        },
        artworkSize =
        maxHeight - pinnedHeight - WindowInsets.statusBars.asPaddingValues(LocalDensity.current).calculateTopPadding(),
        description = description,
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
  description: @Composable () -> Unit,
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
          .layoutId("description")
          .padding(end = TopAppBarHorizontalPadding, start = 4.dp, top = 12.dp)
          .alpha(scrollPercentage)
      ) {
        CompositionLocalProvider(
          LocalContentColor provides actionIconContentColor,
          content = description
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
          .alpha(scrollPercentage)
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

    val artworkPlaceable =
      measurables.first { it.layoutId == "artwork" }
        .measure(constraints.copy(minWidth = 0, maxWidth = constraints.maxWidth))

    val maxTitleWidth = if (constraints.maxWidth == Constraints.Infinity) {
      constraints.maxWidth
    } else {
      (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width - artworkPlaceable.width - 32)
        .coerceAtLeast(0)
    }

    val titlePlaceable =
      measurables.first { it.layoutId == "title" }
        .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

    val descriptionPlaceable =
      measurables.first { it.layoutId == "description" }
        .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth - 32))

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

    val descriptionBaseline =
      if (descriptionPlaceable[LastBaseline] != AlignmentLine.Unspecified) {
        descriptionPlaceable[LastBaseline]
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
          (constraints.maxWidth - actionIconsPlaceable.width - artworkPlaceable.width)
        else
          (constraints.maxWidth - artworkPlaceable.width - actionIconsPlaceable.width) - 24,
        y = when (artworkVerticalArrangement) {
          Arrangement.Center -> 0
          // Apply bottom padding from the title's baseline only when the Arrangement is
          // "Bottom".
          Arrangement.Bottom ->
            if (titleBottomPadding == 0) layoutHeight - artworkPlaceable.height
            else layoutHeight - artworkPlaceable.height - max(
              titleBottomPadding - 2,
              titleBottomPadding - artworkPlaceable.height + artworkBaseline
            )
          // Arrangement.Top
          else -> 0
        }
      )

      descriptionPlaceable.placeRelative(
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
          ) },
      y = when (titleVerticalArrangement) {
        Arrangement.Center -> (layoutHeight - titlePlaceable.height) / 2
        // Apply bottom padding from the title's baseline only when the Arrangement is
        // "Bottom".
        Arrangement.Bottom ->
          if (titleBottomPadding == 0) layoutHeight - titlePlaceable.height
          else layoutHeight - descriptionPlaceable.height - titlePlaceable.height + 8 - max(
            0,
            titleBottomPadding - descriptionPlaceable.height + descriptionBaseline
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