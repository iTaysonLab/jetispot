package bruhcollective.itaysonlab.jetispot.core.api

import bruhcollective.itaysonlab.jetispot.core.DeviceIdProvider
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import com.spotify.dac.api.v1.proto.DacRequest
import com.spotify.dac.api.v1.proto.DacResponse
import com.spotify.home.dac.viewservice.v1.proto.HomeViewServiceRequest
import retrofit2.http.*
import java.util.*

// TODO: Leave as it right now, later separate into other interfaces
interface SpInternalApi {
  @GET("/homeview/v1/home")
  suspend fun getHomeView(@Query("is_car_connected") carConnected: Boolean): HubResponse

  @GET("/hubview-mobile-v1/browse/{id}")
  suspend fun getBrowseView(@Path("id") pageId: String = ""): HubResponse

  @GET("/album-entity-view/v2/album/{id}")
  suspend fun getAlbumView(@Path("id") pageId: String, @Query("checkDeviceCapability") checkDeviceCapability: Boolean = true): HubResponse

  @GET("/artistview/v1/artist/{id}")
  suspend fun getArtistView(@Path("id") pageId: String, @Query("purchase_allowed") purchaseAllowed: Boolean = false, @Query("timeFormat") timeFormat: String = "24h"): HubResponse

  @GET("/artistview/v1/artist/{id}/releases")
  suspend fun getReleasesView(@Path("id") pageId: String, @Query("checkDeviceCapability") checkDeviceCapability: Boolean = true): HubResponse

  @GET("/listening-history/v2/mobile/{timestamp}")
  suspend fun getListeningHistory(@Path("timestamp") timestamp: String = "", @Query("type") type: String = "merged", @Query("last_component_had_play_context") idk: Boolean = false): HubResponse

  @POST("/home-dac-viewservice/v1/view")
  suspend fun getDacHome(@Body request: DacRequest = DacRequest.newBuilder().apply {
    uri = "dac:home"
    featureRequest = com.google.protobuf.Any.pack(HomeViewServiceRequest.newBuilder().apply {
      facet = ""
      clientTimezone = TimeZone.getDefault().id
    }.build())
    clientInfo = DacRequest.ClientInfo.newBuilder().apply {
      appName = "ANDROID_MUSIC_APP"
      version = DeviceIdProvider.SPOTIFY_APP_VERSION
    }.build()
  }.build()): DacResponse

  @GET("/pam-view-service/v1/AllPlans")
  suspend fun getAllPlans(): DacResponse

  @GET("/pam-view-service/v1/PlanOverview")
  suspend fun getPlanOverview(): DacResponse


}