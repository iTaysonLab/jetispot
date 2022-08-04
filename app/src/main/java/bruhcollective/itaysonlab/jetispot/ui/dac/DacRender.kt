package bruhcollective.itaysonlab.jetispot.ui.dac

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.*
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.*
import com.google.protobuf.Message
import com.spotify.allplans.v1.DisclaimerComponent
import com.spotify.allplans.v1.PlanComponent
import com.spotify.home.dac.component.v1.proto.*
import com.spotify.planoverview.v1.*

@Composable
fun DacRender (
  item: Message
) {
  when (item) {
    // AllPlans / PlanOverview
    is MultiUserMemberComponent -> MultiUserMemberComponentBinder(item)
    is BenefitListComponent -> BenefitListComponentBinder(item)
    is PlanComponent -> PlanComponentBinder(item)
    is DisclaimerComponent -> DisclaimerComponentBinder(item)
    is SingleUserRecurringComponent -> SingleUserComponentBinder(item)
    is SingleUserPrepaidComponent -> SingleUserComponentBinder(item)
    is SingleUserTrialComponent -> SingleUserComponentBinder(item)
    // Home
    is ToolbarComponent -> ToolbarComponentBinder(item)
    is ShortcutsSectionComponent -> ShortcutsBinder(item)
    is AlbumCardActionsSmallComponent -> SmallActionCardBinder(title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "album", playCommand = item.playCommand)
    is ArtistCardActionsSmallComponent -> SmallActionCardBinder(title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.followUri, imageUri = item.imageUri, imagePlaceholder = "artist", playCommand = item.playCommand)
    is PlaylistCardActionsSmallComponent -> SmallActionCardBinder(title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "playlist", playCommand = item.playCommand)
    is RecsplanationHeadingComponent -> RecsplanationHeadingComponentBinder(item)
    is SectionHeaderComponent -> SectionHeaderComponentBinder(item.title)
    is SectionComponent -> SectionComponentBinder(item)
    is RecentlyPlayedSectionComponent -> RecentlyPlayedSectionComponentBinder()
    // is SnappyGridSectionComponent -> SnappyGridSectionComponentBinder(item)
    // Other
    else -> {
      Text("DAC proto-known, but UI-unknown component: ${item::class.java.simpleName}\n\n${item}")
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}