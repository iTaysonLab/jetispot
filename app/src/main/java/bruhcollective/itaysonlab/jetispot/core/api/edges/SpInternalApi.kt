package bruhcollective.itaysonlab.jetispot.core.api.edges

import bruhcollective.itaysonlab.jetispot.core.api.SpApiExecutor
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpInternalApi @Inject constructor(
  private val api: SpApiExecutor
) {
  suspend fun getHomeView() = api.getJson<HubResponse>(SpApiExecutor.Edge.Internal, "/homeview/v1/home", mapOf(
    "platform" to "android",
    "client-timezone" to TimeZone.getDefault().id,
    "locale" to api.sessionManager.session.preferredLocale(),
    "video" to "true",
    "podcast" to "true",
    "is_car_connected" to "false"
  ))

  suspend fun getBrowseView(pageId: String = "") = api.getJson<HubResponse>(SpApiExecutor.Edge.Internal, "/hubview-mobile-v1/browse/$pageId", mapOf(
    "platform" to "android",
    "client-timezone" to TimeZone.getDefault().id,
    "locale" to api.sessionManager.session.preferredLocale(),
    "podcast" to "true"
  ))

  suspend fun getAlbumView(id: String = "") = api.getJson<HubResponse>(SpApiExecutor.Edge.Internal, "/album-entity-view/v2/album/$id", mapOf(
    "platform" to "android",
    "client-timezone" to TimeZone.getDefault().id,
    "locale" to api.sessionManager.session.preferredLocale(),
    "video" to "true",
    "podcast" to "true",
    "application" to "nft",
    "checkDeviceCapability" to "true"
  ))

  suspend fun getArtistView(id: String = "") = api.getJson<HubResponse>(SpApiExecutor.Edge.Internal, "/artistview/v1/artist/$id", mapOf(
    "platform" to "android",
    "client-timezone" to TimeZone.getDefault().id,
    "locale" to api.sessionManager.session.preferredLocale(),
    "podcast" to "true",
    "video" to "true",
    "purchase_allowed" to "false",
    "timeFormat" to "24h"
  ))
}