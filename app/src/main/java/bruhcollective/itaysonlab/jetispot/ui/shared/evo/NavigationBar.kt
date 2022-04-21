package bruhcollective.itaysonlab.jetispot.ui.shared.evo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * ![Navigation bar image](https://developer.android.com/images/reference/androidx/compose/material3/navigation-bar.png)
 *
 * Material Design bottom navigation bar.
 *
 * A bottom navigation bar allows switching between primary destinations in an app.
 *
 * [NavigationBar] should contain three to five [NavigationBarItem]s, each representing a singular
 * destination.
 *
 * A simple example looks like:
 * @sample androidx.compose.material3.samples.NavigationBarSample
 *
 * See [NavigationBarItem] for configuration specific to each item, and not the overall
 * [NavigationBar] component.
 *
 * @param modifier optional [Modifier] for this NavigationBar
 * @param containerColor the container color for this NavigationBar
 * @param contentColor the preferred content color provided by this NavigationBar to its children.
 * Defaults to either the matching content color for [containerColor], or if [containerColor] is not
 * a color from the theme, this will keep the same value set above this NavigationBar.
 * @param tonalElevation When [containerColor] is [ColorScheme.surface], a higher tonal elevation
 * value will result in a darker color in light theme and lighter color in dark theme. See also:
 * [Surface].
 * @param content destinations inside this NavigationBar. This should contain multiple
 * [NavigationBarItem]s
 */
@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = 3.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
                .height(NavigationBarHeight)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceBetween,
            content = content
        )
    }
}

private val NavigationBarHeight: Dp = 80.dp
