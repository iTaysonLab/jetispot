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

//TODO: FIX UNSUPPORTED ID IN LISTENING HISTORY - BOBBYESP

@Composable
fun HubBinder (
  item: HubItem,
  isRenderingInGrid: Boolean = false,
) {
  when (item.component) {
    HubComponent.HomeShortSectionHeader -> HomeSectionHeader(item.text!!)
    HubComponent.HomeLargeSectionHeader -> HomeSectionLargeHeader(item)
    HubComponent.GlueSectionHeader -> SectionHeader(item.text!!)
    HubComponent.ShortcutsContainer -> ShortcutsContainer(item.children!!)
    HubComponent.ShortcutsCard -> ShortcutsCard(item)
    HubComponent.FindCard -> FindCard(item)

    HubComponent.SingleFocusCard -> SingleFocusCard(item)

    HubComponent.Carousel -> Carousel(item)

    HubComponent.MediumCard -> {
      if (isRenderingInGrid) {
        GridMediumCard(item)
      } else {
        MediumCard(item)
      }
    }

    HubComponent.ArtistLikedSongs -> LikedSongsRow(item)

    HubComponent.AlbumTrackRow -> AlbumTrackRow(item)
    HubComponent.ArtistTrackRow -> ArtistTrackRow(item)
    HubComponent.PlaylistTrackRow -> PlaylistTrackRow(item)

    HubComponent.ArtistPinnedItem -> ArtistPinnedItem(item)
    HubComponent.AlbumHeader -> AlbumHeader(item)
    HubComponent.ArtistHeader -> ArtistHeader(item)
    HubComponent.LargerRow -> LargerRow(item)

    HubComponent.PlaylistHeader -> PlaylistHeader(item)
    HubComponent.LargePlaylistHeader -> LargePlaylistHeader(item)
    HubComponent.CollectionHeader -> CollectionHeader(item)

    HubComponent.TextRow -> TextRow(item.text!!)
    HubComponent.ImageRow -> ImageRow(item)

    HubComponent.ShowHeader -> ShowHeader(item)
    HubComponent.EpisodeListItem -> EpisodeListItem(item)
    HubComponent.PodcastTopics -> PodcastTopicsStrip(item)

    HubComponent.OutlinedButton -> OutlineButton(item)
    HubComponent.EmptySpace, HubComponent.Ignored -> {}

    else -> {
      Text("Unsupported, id = ${item.id}")
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}