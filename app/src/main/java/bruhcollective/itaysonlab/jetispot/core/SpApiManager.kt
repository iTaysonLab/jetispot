package bruhcollective.itaysonlab.jetispot.core

import bruhcollective.itaysonlab.jetispot.core.api.edges.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.api.edges.SpPartnersApi
import bruhcollective.itaysonlab.jetispot.core.api.edges.SpPublicApi
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("BlockingMethodInNonBlockingContext", "OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalStdlibApi::class)
@Singleton
class SpApiManager @Inject constructor(
  val internal: SpInternalApi,
  val public: SpPublicApi,
  val partners: SpPartnersApi
) {

}