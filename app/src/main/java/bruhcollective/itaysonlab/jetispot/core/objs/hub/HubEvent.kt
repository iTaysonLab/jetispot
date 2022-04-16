package bruhcollective.itaysonlab.jetispot.core.objs.hub

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.DefaultObject
import dev.zacsweers.moshix.sealed.annotations.TypeLabel

@JsonClass(generateAdapter = true, generator = "sealed:name")
sealed class HubEvent {
  @JsonClass(generateAdapter = true)
  @TypeLabel("navigate")
  class NavigateToUri (
    val data: NavigateUri
  ): HubEvent()

  @JsonClass(generateAdapter = true)
  @TypeLabel("playFromContext")
  class PlayFromContext (
    val data: PlayFromContextData
  ): HubEvent()

  @DefaultObject
  object Unknown: HubEvent()
}