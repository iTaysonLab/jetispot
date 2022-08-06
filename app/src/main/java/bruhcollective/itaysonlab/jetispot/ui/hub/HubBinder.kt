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
  delegate: HubScreenDelegate,
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
    HubComponent.HomeShortSectionHeader -> { if (everythingElse) HomeSectionHeader(item.text!!, delegate) }
    HubComponent.HomeLargeSectionHeader -> { if (everythingElse) HomeSectionLargeHeader(delegate, item) }
    HubComponent.GlueSectionHeader -> { if (everythingElse) SectionHeader(item.text!!, delegate) }
    HubComponent.ShortcutsContainer -> { if (everythingElse) ShortcutsContainer(delegate, item.children!!) }
    HubComponent.ShortcutsCard -> { if (everythingElse) ShortcutsCard(delegate, item) }
    HubComponent.FindCard -> { if (everythingElse) FindCard(delegate, item) }

    HubComponent.SingleFocusCard -> { if (everythingElse) SingleFocusCard(delegate, item) }

    HubComponent.Carousel -> { if (everythingElse) Carousel(delegate, item) }
    HubComponent.MediumCard -> {
      if (isRenderingInGrid) {
        run { if (everythingElse) GridMediumCard(delegate, item) }
      } else {
        run { if (everythingElse) MediumCard(delegate, item) }
      }
    }

    HubComponent.ArtistLikedSongs -> { if (everythingElse) LikedSongsRow(delegate, item) }

    HubComponent.AlbumTrackRow -> { if (everythingElse) AlbumTrackRow(delegate, item) }
    HubComponent.ArtistTrackRow -> { if (everythingElse) ArtistTrackRow(delegate, item) }
    HubComponent.PlaylistTrackRow -> { if (everythingElse) PlaylistTrackRow(delegate, item) }

    HubComponent.ArtistPinnedItem -> { if (everythingElse) ArtistPinnedItem(delegate, item) }
    HubComponent.AlbumHeader -> {
      if (albumHeader && !everythingElse) AlbumHeader(delegate, item, scrollBehavior)
      if (showFAB) PlayFAB(delegate, item, scrollBehavior)
    }

    // this way we can probably compose screens classic compose style for all non-server based
    // layout elements
    HubComponent.ArtistHeader -> { if (artistHeader) ArtistHeader(item, scrollBehavior) }

    HubComponent.LargerRow -> { if (everythingElse) LargerRow(delegate, item) }

    HubComponent.PlaylistHeader -> {
      if (everythingElse) PlaylistHeader(delegate, item, scrollBehavior)
      if (showFAB) PlayFAB(delegate, item, scrollBehavior)
    }
    HubComponent.LargePlaylistHeader -> { if (everythingElse) LargePlaylistHeader(item, scrollBehavior) }
    HubComponent.CollectionHeader -> {
      if (everythingElse) CollectionHeader(delegate, item, scrollBehavior)
//      if (showFAB) PlayFAB(navController, delegate, item, scrollBehavior)
    }
    HubComponent.TextRow -> { if (everythingElse) TextRow(item.text!!) }
    HubComponent.ImageRow -> { /* if (everythingElse) ImageRow(navController, delegate, item) */ }

    HubComponent.ShowHeader -> { if (everythingElse) ShowHeader(delegate, item) }
    HubComponent.EpisodeListItem -> { if (everythingElse) EpisodeListItem(delegate, item) }
    HubComponent.PodcastTopics -> { if (everythingElse) PodcastTopicsStrip(delegate, item) }

    HubComponent.OutlinedButton -> { if (everythingElse) OutlineButton(delegate, item) }
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