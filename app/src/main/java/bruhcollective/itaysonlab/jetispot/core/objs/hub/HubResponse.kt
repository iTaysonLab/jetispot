package bruhcollective.itaysonlab.jetispot.core.objs.hub

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class HubResponse (
  val header: HubItem?,
  val body: List<HubItem>,
  val id: String? = null, // album-entity-view
)