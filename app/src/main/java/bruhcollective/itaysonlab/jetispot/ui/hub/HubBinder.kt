package bruhcollective.itaysonlab.jetispot.ui.hub

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubComponent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.components.*

@Composable
fun HubBinder (
  delegate: HubScreenDelegate,
  item: HubItem,
  isRenderingInGrid: Boolean = false,
) {
  when (item.component) {
    HubComponent.HomeShortSectionHeader -> HomeSectionHeader(item.text!!, delegate)
    HubComponent.HomeLargeSectionHeader -> HomeSectionLargeHeader(delegate, item)
    HubComponent.GlueSectionHeader -> SectionHeader(item.text!!, delegate)
    HubComponent.ShortcutsContainer -> ShortcutsContainer(delegate, item.children!!)
    HubComponent.ShortcutsCard -> ShortcutsCard(delegate, item)
    HubComponent.FindCard -> FindCard(delegate, item)

    HubComponent.SingleFocusCard -> SingleFocusCard(delegate, item)

    HubComponent.Carousel -> Carousel(delegate, item)
    HubComponent.MediumCard -> {
      if (isRenderingInGrid) {
        GridMediumCard(delegate, item)
      } else {
        MediumCard(delegate, item)
      }
    }

    HubComponent.ArtistLikedSongs -> LikedSongsRow(delegate, item)

    HubComponent.AlbumTrackRow -> AlbumTrackRow(delegate, item)
    HubComponent.ArtistTrackRow -> ArtistTrackRow(delegate, item)
    HubComponent.PlaylistTrackRow -> PlaylistTrackRow(delegate, item)

    HubComponent.ArtistPinnedItem -> ArtistPinnedItem(delegate, item)
    HubComponent.AlbumHeader -> AlbumHeader(delegate, item)
    HubComponent.ArtistHeader -> ArtistHeader(item)
    HubComponent.LargerRow -> LargerRow(delegate, item)

    HubComponent.PlaylistHeader -> PlaylistHeader(delegate, item)
    HubComponent.LargePlaylistHeader -> LargePlaylistHeader(delegate, item)
    HubComponent.CollectionHeader -> CollectionHeader(delegate, item)

    HubComponent.TextRow -> TextRow(item.text!!)
    HubComponent.ImageRow -> ImageRow(delegate, item)

    HubComponent.ShowHeader -> ShowHeader(delegate, item)
    HubComponent.EpisodeListItem -> EpisodeListItem(delegate, item)
    HubComponent.PodcastTopics -> PodcastTopicsStrip(delegate, item)

    HubComponent.OutlinedButton -> OutlineButton(delegate, item)
    HubComponent.EmptySpace, HubComponent.Ignored -> {}

    else -> {
      Text("Unsupported, id = ${item.id}")
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}