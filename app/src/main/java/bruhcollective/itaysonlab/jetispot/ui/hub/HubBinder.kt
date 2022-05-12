package bruhcollective.itaysonlab.jetispot.ui.hub

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubComponent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.components.*

@Composable
fun HubBinder (
  navController: NavController,
  delegate: HubScreenDelegate,
  item: HubItem
) {
  when (item.component) {
    HubComponent.HomeShortSectionHeader -> HomeSectionHeader(item.text!!, delegate)
    HubComponent.HomeLargeSectionHeader -> HomeSectionLargeHeader(navController, delegate, item)
    HubComponent.GlueSectionHeader -> SectionHeader(item.text!!, delegate)
    HubComponent.ShortcutsContainer -> ShortcutsContainer(navController, delegate, item.children!!)
    HubComponent.ShortcutsCard -> ShortcutsCard(navController, delegate, item)
    HubComponent.FindCard -> FindCard(navController, delegate, item)

    HubComponent.SingleFocusCard -> SingleFocusCard(navController, delegate, item)

    HubComponent.Carousel -> Carousel(navController, delegate, item)
    HubComponent.MediumCard -> MediumCard(navController, delegate, item)

    HubComponent.ArtistLikedSongs -> LikedSongsRow(navController, delegate, item)

    HubComponent.AlbumTrackRow -> AlbumTrackRow(navController, delegate, item)
    HubComponent.ArtistTrackRow -> ArtistTrackRow(navController, delegate, item)
    HubComponent.PlaylistTrackRow -> PlaylistTrackRow(navController, delegate, item)

    HubComponent.ArtistPinnedItem -> ArtistPinnedItem(navController, delegate, item)
    HubComponent.AlbumHeader -> AlbumHeader(navController, delegate, item)
    HubComponent.ArtistHeader -> ArtistHeader(item)
    HubComponent.LargerRow -> LargerRow(navController, delegate, item)

    HubComponent.PlaylistHeader -> PlaylistHeader(navController, delegate, item)
    HubComponent.LargePlaylistHeader -> LargePlaylistHeader(navController, delegate, item)

    HubComponent.TextRow -> TextRow(item.text!!)
    HubComponent.ImageRow -> ImageRow(navController, delegate, item)

    HubComponent.OutlinedButton -> OutlineButton(navController, delegate, item)
    HubComponent.EmptySpace -> {}

    else -> {
      Text("Unsupported, id = ${item.id}")
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}