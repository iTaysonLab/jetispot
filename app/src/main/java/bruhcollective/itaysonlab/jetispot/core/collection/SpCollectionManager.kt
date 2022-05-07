package bruhcollective.itaysonlab.jetispot.core.collection

import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpCollectionApi
import com.spotify.collection2.v2.proto.Collection2V2
import kotlinx.coroutines.*
import javax.inject.Inject

class SpCollectionManager @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val collectionApi: SpCollectionApi
): CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {
  private val pendingWriteOperations = mutableListOf<CollectionWriteOp>()

  sealed class CollectionWriteOp {
    class Add(val spId: String): CollectionWriteOp() { val additionDate = System.currentTimeMillis() / 1000L }
    class Remove(val spId: String): CollectionWriteOp()
  }

  init {
    launch {
      while (true) {
        processPendingWrites()
        delay(5000L)
      }
    }
  }

  private suspend fun processPendingWrites() {
    if (pendingWriteOperations.isEmpty()) return

    /*collectionApi.write(Collection2V2.WriteRequest.newBuilder().apply {
      setUsername("")
      setSet("")
      addAllItems()
      setClientUpdateId(0)
    }.build())*/
  }
}