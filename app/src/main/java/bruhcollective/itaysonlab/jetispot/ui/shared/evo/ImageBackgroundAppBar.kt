package bruhcollective.itaysonlab.jetispot.ui.shared.evo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
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
fun ImageBackgroundTopAppBar(
  title: @Composable () -> Unit,
  picture: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable() (RowScope.() -> Unit) = {},
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  maxHeight: Dp = 152.dp,
  smallTitle: @Composable () -> Unit = {},
  scrollHeight: Float = -1.515f
) {
  TwoRowsTopAppBar(
    title = title,
    picture = picture,
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
    scrollHeight = scrollHeight
  )
}

@Composable
private fun TwoRowsTopAppBar(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit,
  picture: @Composable () -> Unit,
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
  scrollHeight: Float
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
  Box(modifier = modifier.background(color = appBarContainerColor)) {
    Box(
      modifier = Modifier
        .alpha(1f - scrollFraction)
        .height((maxHeightPx + pinnedHeightPx * (scrollHeight - scrollFraction)).dp)
    ) {
      CompositionLocalProvider(content = picture)
      Box(
        Modifier
          .background(
            brush = Brush.verticalGradient(
              listOf(
                MaterialTheme.colorScheme.background.copy(0.4f),
                MaterialTheme.colorScheme.background.copy(0.4f),
                MaterialTheme.colorScheme.background.copy(0.4f),
                MaterialTheme.colorScheme.background.copy(0.4f),
                MaterialTheme.colorScheme.background.copy(0.5f),
                MaterialTheme.colorScheme.background.copy(0.6f),
                MaterialTheme.colorScheme.background.copy(0.7f),
                MaterialTheme.colorScheme.background.copy(0.8f)
              )
            )
          )
          .fillMaxSize()
      ) {}
    }

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
        picture = { },
        pictureSize = 48.dp,
        titleTextStyle = smallTitleTextStyle,
        scrollPercentage = 1f - titleAlpha,
        titleVerticalArrangement = Arrangement.Center,
        titleHorizontalArrangement = Arrangement.Start,
        pictureVerticalArrangement = Arrangement.Center,
        pictureHorizontalArrangement = Arrangement.End,
        titleBottomPadding = 0,
        hideTitleSemantics = hideTopRowSemantics,
        navigationIcon = navigationIcon,
        actions = actionsRow,
        navigationIconVisible = true
      )
      TopAppBarLayout(
        modifier = Modifier.clipToBounds(),
        heightPx = maxHeightPx - pinnedHeightPx + (scrollBehavior?.state?.offset ?: 0f),
        navigationIconContentColor = colors.navigationIconContentColor(scrollFraction).value,
        titleContentColor = colors.titleContentColor(scrollFraction).value,
        actionIconContentColor = colors.actionIconContentColor(scrollFraction).value,
        title = title,
        picture = picture,
        pictureSize = 164.dp,
        titleTextStyle = titleTextStyle,
        scrollPercentage = titleAlpha,
        titleVerticalArrangement = Arrangement.Bottom,
        titleHorizontalArrangement = Arrangement.Start,
        pictureVerticalArrangement = Arrangement.Bottom,
        pictureHorizontalArrangement = Arrangement.End,
        titleBottomPadding = titleBottomPaddingPx,
        hideTitleSemantics = hideBottomRowSemantics,
        navigationIcon = {},
        actions = {},
        navigationIconVisible = false
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
  picture: @Composable () -> Unit,
  pictureSize: Dp,
  titleTextStyle: TextStyle,
  scrollPercentage: Float,
  titleVerticalArrangement: Arrangement.Vertical,
  titleHorizontalArrangement: Arrangement.Horizontal,
  pictureVerticalArrangement: Arrangement.Vertical,
  pictureHorizontalArrangement: Arrangement.Horizontal,
  titleBottomPadding: Int,
  hideTitleSemantics: Boolean,
  navigationIcon: @Composable () -> Unit,
  actions: @Composable () -> Unit,
  navigationIconVisible: Boolean
) {
  val pictureVisible = pictureVerticalArrangement == Arrangement.Center

  Layout(
    {
      Box(
        Modifier
          .layoutId("navigationIcon")
          .padding(start = 8.dp, end = 4.dp)
          .size(if (navigationIconVisible) 38.dp else 0.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp).copy(0.5f))
      ) {
        CompositionLocalProvider(
          LocalContentColor provides navigationIconContentColor,
          content = navigationIcon
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

      // this is needed for the image to load. don't remove it
      Box(
        Modifier
          .layoutId("picture")
          .size(0.dp)
      ) {
        CompositionLocalProvider(
          content = picture
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

    val picturePlaceable =
      measurables.first { it.layoutId == "picture" }
        .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))


    // Locate the title's baseline.
    val titleBaseline =
      if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
        titlePlaceable[LastBaseline]
      } else {
        0
      }

    val pictureBaseline =
      if (picturePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
        picturePlaceable[LastBaseline]
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

      picturePlaceable.placeRelative(
        x = if (pictureVisible)
          (constraints.maxWidth - picturePlaceable.width - actionIconsPlaceable.width)
        else
          (constraints.maxWidth - picturePlaceable.width - actionIconsPlaceable.width),
        y = when (pictureVerticalArrangement) {
          Arrangement.Center -> 0
          // Apply bottom padding from the title's baseline only when the Arrangement is
          // "Bottom".
          Arrangement.Bottom ->
            if (titleBottomPadding == 0) layoutHeight - picturePlaceable.height
            else layoutHeight - picturePlaceable.height - max(
              0,
              titleBottomPadding - picturePlaceable.height + pictureBaseline
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
