package bruhcollective.itaysonlab.jetispot.ui.dac.components_home

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.ui.ext.dynamicUnpack
import com.spotify.home.dac.component.v1.proto.ToolbarComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemListeningHistoryComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemSettingsComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemFeedComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarComponentBinder(
  navController: NavController,
  item: ToolbarComponent
) {
  SmallTopAppBar(title = {
    Text(item.dayPartMessage)
  }, actions = {
    item.itemsList.forEach {
      when (val protoItem = it.dynamicUnpack()) {
        is ToolbarItemFeedComponent -> ToolbarItem(navController, Icons.Default.Notifications, protoItem.navigateUri, protoItem.title)
        is ToolbarItemListeningHistoryComponent -> ToolbarItem(navController, Icons.Default.History, protoItem.navigateUri, protoItem.title)
        is ToolbarItemSettingsComponent -> ToolbarItem(navController, Icons.Default.Settings, protoItem.navigateUri, protoItem.title)
      }
    }
  }, modifier = Modifier.statusBarsPadding())
}

@Composable
private fun ToolbarItem(
  navController: NavController,
  icon: ImageVector,
  navigateTo: String,
  contentDesc: String
) {
  IconButton(onClick = {
    navController.navigate(navigateTo)
  }) {
    Icon(icon, contentDescription = contentDesc)
  }
}