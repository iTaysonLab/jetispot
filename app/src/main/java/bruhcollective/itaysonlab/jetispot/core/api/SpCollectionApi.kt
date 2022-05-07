package bruhcollective.itaysonlab.jetispot.core.api

import com.spotify.collection2.v2.proto.Collection2V2
import retrofit2.http.Body
import retrofit2.http.POST

interface SpCollectionApi {
  @POST("write")
  suspend fun write(@Body data: Collection2V2.WriteRequest)

  @POST("delta")
  suspend fun delta(@Body data: Collection2V2.DeltaRequest): Collection2V2.DeltaResponse

  @POST("paging")
  suspend fun paging(@Body data: Collection2V2.PageRequest): Collection2V2.PageResponse
}