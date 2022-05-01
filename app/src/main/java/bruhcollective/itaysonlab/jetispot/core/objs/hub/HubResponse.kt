package bruhcollective.itaysonlab.jetispot.core.objs.hub

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class HubResponse (
  val title: String? = null,
  val header: HubItem? = null,
  val body: List<HubItem>,
  val id: String? = null, // album-entity-view
)