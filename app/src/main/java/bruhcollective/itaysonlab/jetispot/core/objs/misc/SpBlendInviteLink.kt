package bruhcollective.itaysonlab.jetispot.core.objs.misc

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SpBlendInviteLink(
  val invite: String
)