package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import bruhcollective.itaysonlab.jetispot.ui.ext.dynamicUnpack
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import com.spotify.home.dac.component.v1.proto.ToolbarComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemFeedComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemListeningHistoryComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemSettingsComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarComponentBinder(
  item: ToolbarComponent
) {
  SmallTopAppBar(title = {
    Text(item.dayPartMessage)
  }, actions = {
    item.itemsList.forEach {
      when (val protoItem = it.dynamicUnpack()) {
        is ToolbarItemFeedComponent -> ToolbarItem(Icons.Rounded.Notifications, protoItem.navigateUri, protoItem.title)
        is ToolbarItemListeningHistoryComponent -> ToolbarItem(Icons.Rounded.History, protoItem.navigateUri, protoItem.title)
        is ToolbarItemSettingsComponent -> ToolbarItem(Icons.Rounded.Settings, protoItem.navigateUri, protoItem.title)
      }
    }
  }, modifier = Modifier.statusBarsPadding())
}

@Composable
private fun ToolbarItem(
  icon: ImageVector,
  navigateTo: String,
  contentDesc: String
) {
  val navController = LocalNavigationController.current

  IconButton(onClick = {
    navController.navigate(navigateTo)
  }) {
    Icon(icon, contentDescription = contentDesc)
  }
}