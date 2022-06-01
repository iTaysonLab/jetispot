package bruhcollective.itaysonlab.jetispot.core.api

import bruhcollective.itaysonlab.jetispot.core.objs.misc.SpBlendInviteLink
import retrofit2.http.POST

interface SpBlendApi {
  @POST("/blend-invitation/v1/generate")
  suspend fun generateBlend(): SpBlendInviteLink
}