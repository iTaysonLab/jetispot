package bruhcollective.itaysonlab.jetispot.core.api.edges

import bruhcollective.itaysonlab.jetispot.core.api.SpApiExecutor
import bruhcollective.itaysonlab.jetispot.core.objs.gql.ExtractedColors
import bruhcollective.itaysonlab.jetispot.core.objs.gql.GqlWrap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpPartnersApi @Inject constructor(
  private val api: SpApiExecutor
) {
  suspend fun getDominantColors(picUrl: String) = api.getJson<GqlWrap<ExtractedColors>>(
    SpApiExecutor.Edge.Partner, "/pathfinder/v1/query", mapOf(
      "operationName" to "fetchExtractedColors",
      "variables" to "{\"uris\":[\"$picUrl\"]}",
      "extensions" to "{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"d7696dd106f3c84a1f3ca37225a1de292e66a2d5aced37a66632585eeb3bbbfa\"}}"
    )
  )
}