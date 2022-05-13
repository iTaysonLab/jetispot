package bruhcollective.itaysonlab.jetispot.core.api

import bruhcollective.itaysonlab.jetispot.core.objs.gql.ExtractedColors
import bruhcollective.itaysonlab.jetispot.core.objs.gql.GqlWrap
import retrofit2.http.GET
import retrofit2.http.Query

// TODO: research getExtendedMetadata EXTRACTED_METADATA value and get rid of GraphQL nonsense
interface SpPartnersApi {
  @GET("/pathfinder/v1/query")
  suspend fun fetchExtractedColors(
    @Query("operationName") opName: String = "fetchExtractedColors",
    @Query("extensions") extensions: String = "{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"d7696dd106f3c84a1f3ca37225a1de292e66a2d5aced37a66632585eeb3bbbfa\"}}",
    @Query("variables") variables: String // variables={"uris":["{picUrl}"]}
  ): GqlWrap<ExtractedColors>
}