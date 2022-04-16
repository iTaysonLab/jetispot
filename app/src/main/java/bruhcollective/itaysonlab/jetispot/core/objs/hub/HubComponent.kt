package bruhcollective.itaysonlab.jetispot.core.objs.hub

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.DefaultObject
import dev.zacsweers.moshix.sealed.annotations.TypeLabel

@JsonClass(generateAdapter = true, generator = "sealed:id")
sealed class HubComponent {
  // HOME

  @TypeLabel("home:shortSectionHeader", alternateLabels = ["home:sectionHeader"])
  object HomeShortSectionHeader: HubComponent()

  @TypeLabel("home:tappableSectionHeader")
  object HomeLargeSectionHeader: HubComponent()

  @TypeLabel("home:shortcutsContainer")
  object ShortcutsContainer: HubComponent(), ComponentInGrid

  @TypeLabel("home:shortcutsCard")
  object ShortcutsCard: HubComponent()

  @TypeLabel("home:singleFocusCard")
  object SingleFocusCard: HubComponent()

  // BROWSE

  @TypeLabel("find:categoryCard")
  object FindCard: HubComponent(), ComponentInGrid

  // GLUE

  @TypeLabel("glue:sectionHeader", alternateLabels = ["glue2:sectionHeader"])
  object GlueSectionHeader: HubComponent()

  @TypeLabel("home:carousel", alternateLabels = ["glue:carousel"])
  object Carousel: HubComponent()

  @TypeLabel("home:cardMedium", alternateLabels = ["glue2:card"])
  object MediumCard: HubComponent()

  @TypeLabel("consumerMobile:albumTrackRow")
  object AlbumTrackRow: HubComponent()

  @TypeLabel("glue:textRow")
  object TextRow: HubComponent()

  @TypeLabel("glue2:imageRow")
  object ImageRow: HubComponent()

  @DefaultObject
  object Unknown: HubComponent()
}

interface ComponentInGrid

fun HubComponent.isGrid() = this is ComponentInGrid