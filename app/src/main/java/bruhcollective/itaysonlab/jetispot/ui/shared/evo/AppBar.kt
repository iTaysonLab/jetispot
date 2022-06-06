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

package bruhcollective.itaysonlab.jetispot.ui.shared.evo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * <a href="https://m3.material.io/components/top-app-bar/overview" class="external" target="_blank">Material Design small top app bar</a>.
 *
 * Top app bars display information and actions at the top of a screen.
 *
 * This SmallTopAppBar has slots for a title, navigation icon, and actions.
 *
 * ![Small top app bar image](https://developer.android.com/images/reference/androidx/compose/material3/small-top-app-bar.png)
 *
 * A simple top app bar looks like:
 * @sample androidx.compose.material3.samples.SimpleSmallTopAppBar
 * A top app bar that uses a [scrollBehavior] to customize its nested scrolling behavior when
 * working in conjunction with a scrolling content looks like:
 * @sample androidx.compose.material3.samples.PinnedSmallTopAppBar
 * @sample androidx.compose.material3.samples.EnterAlwaysSmallTopAppBar
 *
 * @param title the title to be displayed in the top app bar
 * @param modifier the [Modifier] to be applied to this top app bar
 * @param navigationIcon the navigation icon displayed at the start of the top app bar. This should
 * typically be an [IconButton] or [IconToggleButton].
 * @param actions the actions displayed at the end of the top app bar. This should typically be
 * [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param colors [TopAppBarColors] that will be used to resolve the colors used for this top app
 * bar in different states. See [TopAppBarDefaults.smallTopAppBarColors].
 * @param scrollBehavior a [TopAppBarScrollBehavior] which holds various offset values that will be
 * applied by this top app bar to set up its height and colors. A scroll behavior is designed to
 * work in conjunction with a scrolled content to change the top app bar appearance as the content
 * scrolls. See [TopAppBarScrollBehavior.nestedScrollConnection].
 */
@Composable
fun SmallTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.smallTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    SingleRowTopAppBar(
        modifier = modifier,
        title = title,
        titleTextStyle = MaterialTheme.typography.titleLarge,
        centeredTitle = false,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors,
        scrollBehavior = scrollBehavior,
        contentPadding = contentPadding,
    )
}

/**
 * <a href="https://m3.material.io/components/top-app-bar/overview" class="external" target="_blank">Material Design center-aligned small top app bar</a>.
 *
 * Top app bars display information and actions at the top of a screen.
 *
 * This small top app bar has a header title that is horizontally aligned to the center.
 *
 * ![Center-aligned top app bar image](https://developer.android.com/images/reference/androidx/compose/material3/center-aligned-top-app-bar.png)
 *
 * This CenterAlignedTopAppBar has slots for a title, navigation icon, and actions.
 *
 * A center aligned top app bar that uses a [scrollBehavior] to customize its nested scrolling
 * behavior when working in conjunction with a scrolling content looks like:
 * @sample androidx.compose.material3.samples.SimpleCenterAlignedTopAppBar
 *
 * @param title the title to be displayed in the top app bar
 * @param modifier the [Modifier] to be applied to this top app bar
 * @param navigationIcon the navigation icon displayed at the start of the top app bar. This should
 * typically be an [IconButton] or [IconToggleButton].
 * @param actions the actions displayed at the end of the top app bar. This should typically be
 * [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param colors [TopAppBarColors] that will be used to resolve the colors used for this top app
 * bar in different states. See [TopAppBarDefaults.centerAlignedTopAppBarColors].
 * @param scrollBehavior a [TopAppBarScrollBehavior] which holds various offset values that will be
 * applied by this top app bar to set up its height and colors. A scroll behavior is designed to
 * work in conjunction with a scrolled content to change the top app bar appearance as the content
 * scrolls. See [TopAppBarScrollBehavior.nestedScrollConnection].
 */
@Composable
fun CenterAlignedTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    SingleRowTopAppBar(
        modifier = modifier,
        title = title,
        titleTextStyle = MaterialTheme.typography.titleLarge,
        centeredTitle = true,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors,
        scrollBehavior = scrollBehavior,
        contentPadding = contentPadding,
    )
}

/**
 * <a href="https://m3.material.io/components/top-app-bar/overview" class="external" target="_blank">Material Design medium top app bar</a>.
 *
 * Top app bars display information and actions at the top of a screen.
 *
 * ![Medium top app bar image](https://developer.android.com/images/reference/androidx/compose/material3/medium-top-app-bar.png)
 *
 * This MediumTopAppBar has slots for a title, navigation icon, and actions. In its default expanded
 * state, the title is displayed in a second row under the navigation and actions.
 *
 * A medium top app bar that uses a [scrollBehavior] to customize its nested scrolling behavior when
 * working in conjunction with scrolling content looks like:
 * @sample androidx.compose.material3.samples.ExitUntilCollapsedMediumTopAppBar
 *
 * @param title the title to be displayed in the top app bar. This title will be used in the app
 * bar's expanded and collapsed states, although in its collapsed state it will be composed with a
 * smaller sized [TextStyle]
 * @param modifier the [Modifier] to be applied to this top app bar
 * @param navigationIcon the navigation icon displayed at the start of the top app bar. This should
 * typically be an [IconButton] or [IconToggleButton].
 * @param actions the actions displayed at the end of the top app bar. This should typically be
 * [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param colors [TopAppBarColors] that will be used to resolve the colors used for this top app
 * bar in different states. See [TopAppBarDefaults.mediumTopAppBarColors].
 * @param scrollBehavior a [TopAppBarScrollBehavior] which holds various offset values that will be
 * applied by this top app bar to set up its height and colors. A scroll behavior is designed to
 * work in conjunction with a scrolled content to change the top app bar appearance as the content
 * scrolls. See [TopAppBarScrollBehavior.nestedScrollConnection].
 */
@Composable
fun MediumTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    TwoRowsTopAppBar(
        modifier = modifier,
        title = title,
        titleTextStyle = MaterialTheme.typography.headlineSmall,
        smallTitleTextStyle = MaterialTheme.typography.titleLarge,
        titleBottomPadding = MediumTitleBottomPadding,
        smallTitle = title,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors,
        maxHeight = 112.dp,
        pinnedHeight = 64.dp,
        scrollBehavior = scrollBehavior,
        contentPadding = contentPadding,
    )
}

/**
 * <a href="https://m3.material.io/components/top-app-bar/overview" class="external" target="_blank">Material Design large top app bar</a>.
 *
 * Top app bars display information and actions at the top of a screen.
 *
 * ![Large top app bar image](https://developer.android.com/images/reference/androidx/compose/material3/large-top-app-bar.png)
 *
 * This LargeTopAppBar has slots for a title, navigation icon, and actions. In its default expanded
 * state, the title is displayed in a second row under the navigation and actions.
 *
 * A large top app bar that uses a [scrollBehavior] to customize its nested scrolling behavior when
 * working in conjunction with scrolling content looks like:
 * @sample androidx.compose.material3.samples.ExitUntilCollapsedLargeTopAppBar
 *
 * @param title the title to be displayed in the top app bar. This title will be used in the app
 * bar's expanded and collapsed states, although in its collapsed state it will be composed with a
 * smaller sized [TextStyle]
 * @param modifier the [Modifier] to be applied to this top app bar
 * @param navigationIcon the navigation icon displayed at the start of the top app bar. This should
 * typically be an [IconButton] or [IconToggleButton].
 * @param actions the actions displayed at the end of the top app bar. This should typically be
 * [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param colors [TopAppBarColors] that will be used to resolve the colors used for this top app
 * bar in different states. See [TopAppBarDefaults.largeTopAppBarColors].
 * @param scrollBehavior a [TopAppBarScrollBehavior] which holds various offset values that will be
 * applied by this top app bar to set up its height and colors. A scroll behavior is designed to
 * work in conjunction with a scrolled content to change the top app bar appearance as the content
 * scrolls. See [TopAppBarScrollBehavior.nestedScrollConnection].
 */
@Composable
fun LargeTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    TwoRowsTopAppBar(
        title = title,
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

/**
 * A single-row top app bar that is designed to be called by the small and center aligned top app
 * bar composables.
 *
 * This SingleRowTopAppBar has slots for a title, navigation icon, and actions. When the
 * [centeredTitle] flag is true, the title will be horizontally aligned to the center of the top app
 * bar width.
 */
@Composable
private fun SingleRowTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    centeredTitle: Boolean,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    colors: TopAppBarColors,
    scrollBehavior: TopAppBarScrollBehavior?,
    contentPadding: PaddingValues,
) {
    // TODO(b/182393826): Check if there is a better place to set the offsetLimit.
    // Set a scroll offset limit to hide the entire app bar area when scrolling.
    val offsetLimit = with(LocalDensity.current) { -64.dp.toPx() }
    SideEffect {
        if (scrollBehavior?.state?.offsetLimit != offsetLimit) {
            scrollBehavior?.state?.offsetLimit = offsetLimit
        }
    }

    // Obtain the container color from the TopAppBarColors.
    // This may potentially animate or interpolate a transition between the container-color and the
    // container's scrolled-color according to the app bar's scroll state.
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
    // Compose a Surface with a TopAppBarLayout content. The surface's background color will be
    // animated as specified above, and the height of the app bar will be determined by the current
    // scroll-state offset.
    Surface(modifier = modifier, color = appBarContainerColor) {
        val height = LocalDensity.current.run {
            64.dp.toPx() + (scrollBehavior?.state?.offset ?: 0f)
        }
        TopAppBarLayout(
            modifier = Modifier.padding(contentPadding),
            heightPx = height,
            navigationIconContentColor = colors.navigationIconContentColor(scrollFraction).value,
            titleContentColor = colors.titleContentColor(scrollFraction).value,
            actionIconContentColor = colors.actionIconContentColor(scrollFraction).value,
            title = title,
            titleTextStyle = titleTextStyle,
            titleAlpha = 1f,
            titleVerticalArrangement = Arrangement.Center,
            titleHorizontalArrangement =
            if (centeredTitle) Arrangement.Center else Arrangement.Start,
            titleBottomPadding = 0,
            hideTitleSemantics = false,
            navigationIcon = navigationIcon,
            actions = actionsRow,
        )
    }
}

/**
 * A two-rows top app bar that is designed to be called by the Large and Medium top app bar
 * composables.
 *
 * @throws [IllegalArgumentException] if the given [maxHeight] is equal or smaller than the
 * [pinnedHeight]
 */
@Composable
private fun TwoRowsTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    titleBottomPadding: Dp,
    smallTitle: @Composable () -> Unit,
    smallTitleTextStyle: TextStyle,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    colors: TopAppBarColors,
    maxHeight: Dp,
    pinnedHeight: Dp,
    scrollBehavior: TopAppBarScrollBehavior?,
    contentPadding: PaddingValues,
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
                navigationIconContentColor =
                colors.navigationIconContentColor(scrollFraction).value,
                titleContentColor = colors.titleContentColor(scrollFraction).value,
                actionIconContentColor = colors.actionIconContentColor(scrollFraction).value,
                title = smallTitle,
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
                heightPx = maxHeightPx - pinnedHeightPx + (scrollBehavior?.state?.offset ?: 0f),
                navigationIconContentColor =
                colors.navigationIconContentColor(scrollFraction).value,
                titleContentColor = colors.titleContentColor(scrollFraction).value,
                actionIconContentColor = colors.actionIconContentColor(scrollFraction).value,
                title = title,
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

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = (layoutHeight - actionIconsPlaceable.height) / 2
            )
        }
    }
}


/**
 * A two-rows top app bar that is designed to be called by the Large and Medium top app bar
 * composables.
 *
 * @throws [IllegalArgumentException] if the given [maxHeight] is equal or smaller than the
 * [pinnedHeight]
 */
@Composable
private fun TwoRowsTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    titleBottomPadding: Dp,
    smallTitle: @Composable () -> Unit,
    smallTitleTextStyle: TextStyle,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    colors: TopAppBarColors,
    maxHeight: Dp,
    pinnedHeight: Dp,
    scrollBehavior: TopAppBarScrollBehavior?
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
        Column {
            TopAppBarLayout(
                modifier = Modifier,
                heightPx = pinnedHeightPx,
                navigationIconContentColor =
                colors.navigationIconContentColor(scrollFraction).value,
                titleContentColor = colors.titleContentColor(scrollFraction).value,
                actionIconContentColor = colors.actionIconContentColor(scrollFraction).value,
                title = smallTitle,
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
                heightPx = maxHeightPx - pinnedHeightPx + (scrollBehavior?.state?.offset ?: 0f),
                navigationIconContentColor =
                colors.navigationIconContentColor(scrollFraction).value,
                titleContentColor = colors.titleContentColor(scrollFraction).value,
                actionIconContentColor = colors.actionIconContentColor(scrollFraction).value,
                title = title,
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

private val MediumTitleBottomPadding = 24.dp
private val LargeTitleBottomPadding = 28.dp
private val TopAppBarHorizontalPadding = 4.dp

// TODO: this should probably be part of the touch target of the start and end icons, clarify this
private val AppBarHorizontalPadding = 4.dp

// A title inset when the App-Bar is a Medium or Large one. Also used to size a spacer when the
// navigation icon is missing.
private val TopAppBarTitleInset = 16.dp - TopAppBarHorizontalPadding

private const val TopAppBarAnimationDurationMillis = 500