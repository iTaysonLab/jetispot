package bruhcollective.itaysonlab.jetispot.core.api.edges

import bruhcollective.itaysonlab.jetispot.core.api.SpApiExecutor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpPublicApi @Inject constructor(
  private val api: SpApiExecutor
): SpEdgeScope by SpApiExecutor.Edge.Public.scope(api) {

}