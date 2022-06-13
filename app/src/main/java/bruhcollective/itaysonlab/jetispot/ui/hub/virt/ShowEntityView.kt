package bruhcollective.itaysonlab.jetispot.ui.hub.virt

import bruhcollective.itaysonlab.jetispot.core.SpMetadataRequester
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse

object ShowEntityView {
  suspend fun create(
    spSessionManager: SpSessionManager,
    metadataRequester: SpMetadataRequester,
    id: String
  ): HubResponse {
    TODO()
  }
}