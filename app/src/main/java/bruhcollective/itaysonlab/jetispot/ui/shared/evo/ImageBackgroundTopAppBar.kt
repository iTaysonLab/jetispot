package bruhcollective.itaysonlab.jetispot.ui.shared.evo

/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
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
import bruhcollective.itaysonlab.jetispot.ui.ext.blendWith
import kotlin.math.max
import kotlin.math.roundToInt


@ExperimentalMaterial3Api
@Composable
fun ImageBackgroundTopAppBar(
  title: @Composable () -> Unit,
  smallTitle: @Composable () -> Unit = {},
  description: @Composable () -> Unit = {},
  picture: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
  windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
  navigationIcon: @Composable () -> Unit = {},
  actions: @Composable() (RowScope.() -> Unit) = {},
  colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
  scrollBehavior: TopAppBarScrollBehavior? = null,
  maxHeight: Dp = 152.dp,
  gradient: Boolean = true,
  navigationIconPresent: Boolean,
) {
  TwoRowsTopAppBar(
    title = title,
    description = description,
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
    gradient = gradient,
    windowInsets = windowInsets,
    navigationIconVisible = navigationIconPresent,
  )
}

/**
 * A two-rows top app bar that is designed to be called by the Large and Medium top app bar
 * composables.
 *
 * @throws [IllegalArgumentException] if the given [maxHeight] is equal or smaller than the
 * [pinnedHeight]
 */
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
  windowInsets: WindowInsets,
  colors: TopAppBarColors,
  maxHeight: Dp,
  pinnedHeight: Dp,
  scrollBehavior: TopAppBarScrollBehavior?,
  description: @Composable () -> Unit,
  picture: @Composable () -> Unit,
  gradient: Boolean,
  navigationIconVisible: Boolean,
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

  // Sets the app bar's height offset limit to hide just the bottom title area and keep top title
  // visible when collapsed.
  SideEffect {
    if (scrollBehavior?.state?.heightOffsetLimit != pinnedHeightPx - maxHeightPx) {
      scrollBehavior?.state?.heightOffsetLimit = pinnedHeightPx - maxHeightPx
    }
  }

  // Obtain the container Color from the TopAppBarColors using the `collapsedFraction`, as the
  // bottom part of this TwoRowsTopAppBar changes color at the same rate the app bar expands or
  // collapse.
  // This will potentially animate or interpolate a transition between the container color and the
  // container's scrolled color according to the app bar's scroll state.
  val colorTransitionFraction = when (scrollBehavior!!.state.collapsedFraction.toInt()) {
    1 -> scrollBehavior.state.collapsedFraction
    0 -> scrollBehavior.state.collapsedFraction
    else -> 0f
  }
  val appBarContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp).copy(scrollBehavior.state.collapsedFraction)

  // Wrap the given actions in a Row.
  val actionsRow = @Composable {
    Row(
      horizontalArrangement = Arrangement.End,
      verticalAlignment = Alignment.CenterVertically,
      content = actions
    )
  }
  val titleAlpha = 1f - colorTransitionFraction
  // Hide the top row title semantics when its alpha value goes below 0.5 threshold.
  // Hide the bottom row title semantics when the top title semantics are active.
  val hideTopRowSemantics = colorTransitionFraction < 0.5f
  val hideBottomRowSemantics = !hideTopRowSemantics

  // Set up support for resizing the top app bar when vertically dragging the bar itself.
  val appBarDragModifier = if (scrollBehavior != null && !scrollBehavior.isPinned) {
    Modifier.draggable(
      orientation = Orientation.Vertical,
      state = rememberDraggableState { delta ->
        scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffset + delta
      },
      onDragStopped = { snapTopAppBar(scrollBehavior.state) }
    )
  } else {
    Modifier
  }

  Box(modifier = modifier.background(color = appBarContainerColor).then(appBarDragModifier)) {
    Box(
      modifier = Modifier
        .alpha(titleAlpha)
        .height((maxHeight + 42.dp) * titleAlpha)
    ) {
      CompositionLocalProvider(content = picture)
      Box(
        Modifier
          .background(
            brush = if (gradient) Brush.verticalGradient(
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
            ) else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
          )
          .fillMaxSize()
      ) {}
    }

    val colors = MaterialTheme.colorScheme
    Column(
      Modifier
        .windowInsetsPadding(windowInsets)
        // clip after padding so we don't know the title over the inset area
        .clipToBounds()
    ) {
      TopAppBarLayout(
        modifier = Modifier,
        heightPx = pinnedHeightPx,
        navigationIconContentColor = colors.onBackground,
        titleContentColor = colors.onBackground.copy(colorTransitionFraction),
        actionIconContentColor = colors.onBackground.blendWith(
          colors.onSurfaceVariant, scrollBehavior.state.collapsedFraction
        ),
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
        navigationIconVisible = navigationIconVisible,
      )
      TopAppBarLayout(
        modifier = Modifier.clipToBounds(),
        heightPx = maxHeightPx - pinnedHeightPx + (scrollBehavior.state.heightOffset ?: 0f),
        navigationIconContentColor = Color.Transparent,
        titleContentColor = colors.onBackground.copy(colorTransitionFraction),
        actionIconContentColor = Color.Transparent,
        title = title,
        description = description,
        titleTextStyle = titleTextStyle,
        titleAlpha = titleAlpha,
        titleVerticalArrangement = Arrangement.Bottom,
        titleHorizontalArrangement = Arrangement.Start,
        titleBottomPadding = titleBottomPaddingPx,
        hideTitleSemantics = hideBottomRowSemantics,
        navigationIcon = {},
        actions = {},
        navigationIconVisible = false,
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
private suspend fun snapTopAppBar(state: TopAppBarState) {
  // In case the app bar motion was stopped in a state where it's partially visible, snap it to
  // the nearest state.
  if (state.heightOffset < 0 &&
    state.heightOffset > state.heightOffsetLimit
  ) {
    AnimationState(initialValue = state.heightOffset).animateTo(
      if (state.collapsedFraction < 0.5f) 0f else state.heightOffsetLimit,
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    ) { state.heightOffset = value }
  }
}

/**
 * The base [Layout] for all top app bars. This function lays out a top app bar navigation icon
 * (leading icon), a title (header), and action icons (trailing icons). Note that the navigation and
 * the actions are optional.
 *
 * @param heightPx the total height this layout is capped to
 * @param navigationIconContentColor the content color that will be applied via a
 * [LocalContentColor] when composing the navigation icon
 * @param titleContentColor the color that will be applied via a [LocalContentColor] when composing
 * the title
 * @param actionIconContentColor the content color that will be applied via a [LocalContentColor]
 * when composing the action icons
 * @param title the top app bar title (header)
 * @param titleTextStyle the title's text style
 * @param modifier a [Modifier]
 * @param titleAlpha the title's alpha
 * @param titleVerticalArrangement the title's vertical arrangement
 * @param titleHorizontalArrangement the title's horizontal arrangement
 * @param titleBottomPadding the title's bottom padding
 * @param hideTitleSemantics hides the title node from the semantic tree. Apply this
 * boolean when this layout is part of a [TwoRowsTopAppBar] to hide the title's semantics
 * from accessibility services. This is needed to avoid having multiple titles visible to
 * accessibility services at the same time, when animating between collapsed / expanded states.
 * @param navigationIcon a navigation icon [Composable]
 * @param actions actions [Composable]
 */
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
  navigationIconVisible: Boolean,
) {
  Layout(
    {
      Box(
        Modifier
          .layoutId("navigationIcon")
          .padding(start = 8.dp, end = 4.dp)
          .size(if (navigationIconVisible) 38.dp else 0.dp)
          .clip(CircleShape)
          .background(
            MaterialTheme.colorScheme
              .surfaceColorAtElevation(4.dp)
              .copy(0.5f)
          )
      ) {
        CompositionLocalProvider(
          LocalContentColor provides navigationIconContentColor,
          content = navigationIcon
        )
      }
      Box(
        Modifier
          .layoutId("description")
          .padding(end = TopAppBarHorizontalPadding, start = 4.dp)
          .alpha(titleAlpha)
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
          .layoutId("actionIcons")
          .padding(end = 8.dp)
      ) {
        Box(modifier = Modifier) {
          CompositionLocalProvider(
            LocalContentColor provides actionIconContentColor,
            content = actions
          )
        }
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
    val descriptionPlaceable =
      measurables.first { it.layoutId == "description" }
        .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))
    val titlePlaceable =
      measurables.first { it.layoutId == "title" }
        .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

    // Locate the title's baseline.
    val titleBaseline =
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
            constraints.maxWidth - titlePlaceable.width - descriptionPlaceable.width
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
            else layoutHeight - descriptionPlaceable.height - titlePlaceable.height + 8 - max(
              0,
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

private const val TopAppBarAnimationDurationMillis = 500
