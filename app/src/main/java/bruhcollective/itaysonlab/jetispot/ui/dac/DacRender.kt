package bruhcollective.itaysonlab.jetispot.ui.dac

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.BuildConfig
import bruhcollective.itaysonlab.jetispot.proto.ErrorComponent
import bruhcollective.itaysonlab.jetispot.ui.dac.components_home.*
import bruhcollective.itaysonlab.jetispot.ui.dac.components_plans.*
import com.google.protobuf.Message
import com.spotify.allplans.v1.DisclaimerComponent
import com.spotify.allplans.v1.PlanComponent
import com.spotify.home.dac.component.heading.v1.proto.RecsplanationHeadingSingleTextComponent
import com.spotify.home.dac.component.v1.proto.*
import com.spotify.home.dac.component.v2.proto.ToolbarComponentV2
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
    is FallbackPlanComponent -> FallbackPlanComponentBinder(item)

    // Home
    is ToolbarComponent -> ToolbarComponentBinder(item)
    is ToolbarComponentV2 -> ToolbarComponent2Binder(item)
    is ShortcutsSectionComponent -> ShortcutsBinder(item)

    is AlbumCardActionsSmallComponent -> SmallActionCardBinder(title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "album", playCommand = item.playCommand)
    is ArtistCardActionsSmallComponent -> SmallActionCardBinder(title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.followUri, imageUri = item.imageUri, imagePlaceholder = "artist", playCommand = item.playCommand)
    is PlaylistCardActionsSmallComponent -> SmallActionCardBinder(title = item.title, subtitle = item.subtitle, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "playlist", playCommand = item.playCommand)
    is AlbumCardActionsMediumComponent -> MediumActionCardBinder(title = item.title, subtitle = item.description, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "album", playCommand = item.playCommand, contentType = item.contentType, fact = item.conciseFact, gradientColor = item.gradientColor)
    is ArtistCardActionsMediumComponent -> MediumActionCardBinder(title = item.title, subtitle = item.description, navigateUri = item.navigateUri, likeUri = item.followUri, imageUri = item.imageUri, imagePlaceholder = "artist", playCommand = item.playCommand, contentType = item.contentType, fact = item.conciseFact, gradientColor = item.gradientColor)
    is PlaylistCardActionsMediumComponent -> MediumActionCardBinder(title = item.title, subtitle = item.description, navigateUri = item.navigateUri, likeUri = item.likeUri, imageUri = item.imageUri, imagePlaceholder = "playlist", playCommand = item.playCommand, contentType = item.contentType, fact = item.conciseFact, gradientColor = item.gradientColor)

    is RecsplanationHeadingComponent -> RecsplanationHeadingComponentBinder(item)
    is RecsplanationHeadingSingleTextComponent -> RecsplanationHeadingSingleTextComponentBinder(item)

    is SectionHeaderComponent -> SectionHeaderComponentBinder(item.title)
    is SectionComponent -> SectionComponentBinder(item)
    is RecentlyPlayedSectionComponent -> RecentlyPlayedSectionComponentBinder()

    //Podcasts
    //EpisodeCardActionsMediumComponent ->

    // is SnappyGridSectionComponent -> SnappyGridSectionComponentBinder(item)
    // Other

    is SnappyGridSectionComponent -> {}

    is ErrorComponent -> {
      if (BuildConfig.DEBUG) {
        Column {
          Text(
            if (item.type == ErrorComponent.ErrorType.UNSUPPORTED) {
              "DAC unsupported component"
            } else {
              "DAC rendering error"
            }, Modifier.padding(horizontal = 16.dp)
          )
          Text(
            item.message ?: "",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
              .padding(top = 4.dp)
              .padding(horizontal = 16.dp)
          )
        }
      }
    }

    else -> {
      if (BuildConfig.DEBUG) {
        Text("DAC proto-known, but UI-unknown component: ${item::class.java.simpleName}\n\n${item}", modifier = Modifier.padding(16.dp))
      }
    }
  }
}