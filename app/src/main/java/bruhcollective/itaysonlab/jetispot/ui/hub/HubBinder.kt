package bruhcollective.itaysonlab.jetispot.ui.hub

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubComponent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import bruhcollective.itaysonlab.jetispot.ui.hub.components.*
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.PlayFAB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubBinder (
  item: HubItem,
  isRenderingInGrid: Boolean = false,
  scrollBehavior: TopAppBarScrollBehavior = rememberEUCScrollBehavior(),
  artistHeader: Boolean = false,
  albumHeader: Boolean = false,
  everythingElse: Boolean = true,
  showFAB: Boolean = false
) {
  val navController = LocalNavigationController.current

  when (item.component) {
    HubComponent.HomeShortSectionHeader -> { if (everythingElse) HomeSectionHeader(item.text!!) }
    HubComponent.HomeLargeSectionHeader -> { if (everythingElse) HomeSectionLargeHeader(item) }
    HubComponent.GlueSectionHeader -> { if (everythingElse) SectionHeader(item.text!!) }
    HubComponent.ShortcutsContainer -> { if (everythingElse) ShortcutsContainer(item.children!!) }
    HubComponent.ShortcutsCard -> { if (everythingElse) ShortcutsCard(item) }
    HubComponent.FindCard -> { if (everythingElse) FindCard( item) }

    HubComponent.SingleFocusCard -> { if (everythingElse) SingleFocusCard(item) }

    HubComponent.Carousel -> { if (everythingElse) Carousel(item) }
    HubComponent.MediumCard -> {
      if (isRenderingInGrid) {
        run { if (everythingElse) GridMediumCard(item) }
      } else {
        run { if (everythingElse) MediumCard(item) }
      }
    }

    HubComponent.ArtistLikedSongs -> { if (everythingElse) LikedSongsRow(item) }

    HubComponent.AlbumTrackRow -> { if (everythingElse) AlbumTrackRow(item) }
    HubComponent.ArtistTrackRow -> { if (everythingElse) ArtistTrackRow(item) }
    HubComponent.PlaylistTrackRow -> { if (everythingElse) PlaylistTrackRow(item) }

    HubComponent.ArtistPinnedItem -> { if (everythingElse) ArtistPinnedItem(item) }
    HubComponent.AlbumHeader -> {
      if (albumHeader && !everythingElse) AlbumHeader(item, scrollBehavior)
    }

    // this way we can probably compose screens classic compose style for all non-server based
    // layout elements
    HubComponent.ArtistHeader -> { if (artistHeader) ArtistHeader(item, scrollBehavior) }

    HubComponent.LargerRow -> { if (everythingElse) LargerRow(item) }

    HubComponent.PlaylistHeader -> {
      if (everythingElse) PlaylistHeader(item, scrollBehavior)
      if (showFAB) PlayFAB(item, scrollBehavior)
    }
    HubComponent.LargePlaylistHeader -> { if (everythingElse) LargePlaylistHeader(item, scrollBehavior) }
    HubComponent.CollectionHeader -> { if (everythingElse) CollectionHeader(item, scrollBehavior) }
    HubComponent.TextRow -> { if (everythingElse) TextRow(item.text!!) }
    HubComponent.ImageRow -> { /* if (everythingElse) ImageRow(navController, item) */ }

    HubComponent.ShowHeader -> { if (everythingElse) ShowHeader(item) }
    HubComponent.EpisodeListItem -> { if (everythingElse) EpisodeListItem(item) }
    HubComponent.PodcastTopics -> { if (everythingElse) PodcastTopicsStrip(item) }

    HubComponent.OutlinedButton -> { if (everythingElse) OutlineButton(item) }
    HubComponent.EmptySpace, HubComponent.Ignored -> {}

    else -> {
      run {
        if (everythingElse) {
          Text("Unsupported, id = ${item.id}")
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    }
  }
}