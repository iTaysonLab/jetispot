package bruhcollective.itaysonlab.jetispot.core.collection

import bruhcollective.itaysonlab.jetispot.core.SpMetadataRequester
import bruhcollective.itaysonlab.jetispot.core.util.Log
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpCollectionApi
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionRepository
import bruhcollective.itaysonlab.swedentricks.protos.CollectionUpdate
import com.spotify.playlist4.Playlist4ApiProto
import kotlinx.coroutines.*
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.dealer.DealerClient
import java.util.concurrent.Executors
import javax.inject.Inject

class SpCollectionManager @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val internalApi: SpInternalApi,
  private val collectionApi: SpCollectionApi,
  private val dbRepository: LocalCollectionRepository,
  private val dao: LocalCollectionDao,
  private val metadataRequester: SpMetadataRequester
): DealerClient.MessageListener {
  // it's important to use queuing here
  private val scopeDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
  private val scope = CoroutineScope(scopeDispatcher + SupervisorJob())

  private val writer = SpCollectionWriter(spSessionManager, internalApi, collectionApi, dbRepository, metadataRequester, dao, scope)

  fun init() {
    spSessionManager.session.dealer().addMessageListener(this, "hm://playlist/v2/user/${spSessionManager.session.username()}/rootlist", "hm://collection/collection/" + spSessionManager.session.username(), "hm://collection/artist/" + spSessionManager.session.username())
    scope.launch {
      scan()
      writer.performContentFiltersScan()
      writer.performRootlistScan()
    }
  }

  // scans network collection for tracks, albums, pins and artists
  suspend fun scan() = withContext(scopeDispatcher) {
    performCollectionScan()
    writer.performScan("artist")
    writer.performScan("ylpin")
    writer.performScan("show")
  }

  suspend fun artists() = withContext(scopeDispatcher) {
    writer.performScan("artist")
    dao.getArtists()
  }

  suspend fun albums() = withContext(scopeDispatcher) {
    performCollectionScan()
    dao.getAlbums()
  }

  suspend fun tracksByArtist(id: String) = withContext(scopeDispatcher) {
    performScanIfEmpty("collection")
    dao.getTracksByArtist(id)
  }

  suspend fun performScanIfEmpty(of: String) {
    Log.d("SpColManager", "Performing scan of $of (if empty)")
    dao.getCollection(of) ?: writer.performScan(of)
  }

  suspend fun performCollectionScan() {
    writer.performScan("collection")
  }

  override fun onMessage(p0: String, p1: MutableMap<String, String>, p2: ByteArray) {
    if (p0.startsWith("hm://playlist/v2/user/")) {
      writer.pubsubUpdateRootlist(Playlist4ApiProto.PlaylistModificationInfo.parseFrom(p2))
    } else if (p0.startsWith("hm://collection/") && !p0.endsWith("/json")) {
      writer.pubsubUpdateCollection(CollectionUpdate.parseFrom(p2))
    } else {
      Log.d("SpColManager", "<onMessage: $p0 / $p1> = ${p2.decodeToString()} / ${Utils.bytesToHex(p2)}")
    }
  }

  suspend fun clean() = dbRepository.clean()
}