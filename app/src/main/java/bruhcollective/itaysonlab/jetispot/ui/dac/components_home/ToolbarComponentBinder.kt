package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.ext.dynamicUnpack
import com.spotify.home.dac.component.v1.proto.ToolbarComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemFeedComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemListeningHistoryComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemSettingsComponent

@Composable
fun ToolbarComponentBinder(
  navController: LambdaNavigationController,
  item: ToolbarComponent
) {
  LargeTopAppBar(
    title = { Text(item.dayPartMessage) },
    actions = {
      item.itemsList.forEach {
        when (val protoItem = it.dynamicUnpack()) {
          is ToolbarItemFeedComponent -> ToolbarItem(navController, Icons.Rounded.Notifications, protoItem.navigateUri, protoItem.title)
          is ToolbarItemListeningHistoryComponent -> ToolbarItem(navController, Icons.Rounded.History, protoItem.navigateUri, protoItem.title)
          is ToolbarItemSettingsComponent -> ToolbarItem(navController, Icons.Rounded.Settings, protoItem.navigateUri, protoItem.title)
        }
      }
    },
    modifier = Modifier.statusBarsPadding()
  )
}

@Composable
private fun ToolbarItem(
  navController: LambdaNavigationController,
  icon: ImageVector,
  navigateTo: String,
  contentDesc: String
) {
  IconButton(onClick = { navController.navigate(navigateTo) }) {
    Icon(icon, contentDescription = contentDesc)
  }
}