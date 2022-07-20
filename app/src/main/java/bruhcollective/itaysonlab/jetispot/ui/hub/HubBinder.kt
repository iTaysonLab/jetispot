package bruhcollective.itaysonlab.jetispot.ui.hub

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubComponent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import bruhcollective.itaysonlab.jetispot.ui.hub.components.*
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.PlayFAB

@Composable
fun HubBinder (
  navController: LambdaNavigationController,
  delegate: HubScreenDelegate,
  item: HubItem,
  isRenderingInGrid: Boolean = false,
  scrollBehavior: TopAppBarScrollBehavior = rememberEUCScrollBehavior(),
  artistHeader: Boolean = false,
  albumHeader: Boolean = false,
  everythingElse: Boolean = true,
  showFAB: Boolean = false
) {
  when (item.component) {
    HubComponent.HomeShortSectionHeader -> { if (everythingElse) HomeSectionHeader(item.text!!, delegate) }
    HubComponent.HomeLargeSectionHeader -> { if (everythingElse) HomeSectionLargeHeader(navController, delegate, item) }
    HubComponent.GlueSectionHeader -> { if (everythingElse) SectionHeader(item.text!!, delegate) }
    HubComponent.ShortcutsContainer -> { if (everythingElse) ShortcutsContainer(navController, delegate, item.children!!) }
    HubComponent.ShortcutsCard -> { if (everythingElse) ShortcutsCard(navController, delegate, item) }
    HubComponent.FindCard -> { if (everythingElse) FindCard(navController, delegate, item) }

    HubComponent.SingleFocusCard -> { if (everythingElse) SingleFocusCard(navController, delegate, item) }

    HubComponent.Carousel -> { if (everythingElse) Carousel(navController, delegate, item) }
    HubComponent.MediumCard -> {
      if (isRenderingInGrid) {
        run { if (everythingElse) GridMediumCard(navController, delegate, item) }
      } else {
        run { if (everythingElse) MediumCard(navController, delegate, item) }
      }
    }

    HubComponent.ArtistLikedSongs -> { if (everythingElse) LikedSongsRow(navController, delegate, item) }

    HubComponent.AlbumTrackRow -> { if (everythingElse) AlbumTrackRow(navController, delegate, item) }
    HubComponent.ArtistTrackRow -> { if (everythingElse) ArtistTrackRow(navController, delegate, item) }
    HubComponent.PlaylistTrackRow -> { if (everythingElse) PlaylistTrackRow(navController, delegate, item) }

    HubComponent.ArtistPinnedItem -> { if (everythingElse) ArtistPinnedItem(navController, delegate, item) }
    HubComponent.AlbumHeader -> {
      if (albumHeader && !everythingElse) AlbumHeader(navController, delegate, item, scrollBehavior)
      if (showFAB) PlayFAB(navController, delegate, item, scrollBehavior)
    }

    // this way we can probably compose screens classic compose style for all non-server based
    // layout elements
    HubComponent.ArtistHeader -> { if (artistHeader) ArtistHeader(item, scrollBehavior, navController) }

    HubComponent.LargerRow -> { if (everythingElse) LargerRow(navController, delegate, item) }

    HubComponent.PlaylistHeader -> {
      if (everythingElse) PlaylistHeader(navController, delegate, item, scrollBehavior)
      if (showFAB) PlayFAB(navController, delegate, item, scrollBehavior)
    }
    HubComponent.LargePlaylistHeader -> { if (everythingElse) LargePlaylistHeader(navController, delegate, item, scrollBehavior) }
    HubComponent.CollectionHeader -> {
      if (everythingElse) CollectionHeader(navController, delegate, item, scrollBehavior)
      if (showFAB) PlayFAB(navController, delegate, item, scrollBehavior)
    }
    HubComponent.TextRow -> { if (everythingElse) TextRow(item.text!!) }
    HubComponent.ImageRow -> { /* if (everythingElse) ImageRow(navController, delegate, item) */ }

    HubComponent.ShowHeader -> { if (everythingElse) ShowHeader(navController, delegate, item) }
    HubComponent.EpisodeListItem -> { if (everythingElse) EpisodeListItem(navController, delegate, item) }
    HubComponent.PodcastTopics -> { if (everythingElse) PodcastTopicsStrip(navController, delegate, item) }

    HubComponent.OutlinedButton -> { if (everythingElse) OutlineButton(navController, delegate, item) }
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