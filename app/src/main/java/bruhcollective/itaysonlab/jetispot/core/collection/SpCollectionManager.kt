package bruhcollective.itaysonlab.jetispot.core.collection

import android.util.Log
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpCollectionApi
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionRepository
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionAlbum
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtist
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionArtistMetadata
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionTrack
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import bruhcollective.itaysonlab.jetispot.core.util.Revision
import bruhcollective.itaysonlab.swedentricks.protos.CollectionUpdate
import bruhcollective.itaysonlab.swedentricks.protos.CollectionUpdateEntry
import com.google.protobuf.ByteString
import com.spotify.collection2.v2.proto.Collection2V2
import com.spotify.extendedmetadata.ExtendedMetadata
import com.spotify.extendedmetadata.ExtensionKindOuterClass
import com.spotify.metadata.Metadata
import com.spotify.playlist4.Playlist4ApiProto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.dealer.DealerClient
import xyz.gianlu.librespot.metadata.*
import java.util.concurrent.Executors
import javax.inject.Inject

class SpCollectionManager @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val internalApi: SpInternalApi,
  private val collectionApi: SpCollectionApi,
  private val dbRepository: LocalCollectionRepository,
  private val dao: LocalCollectionDao
): DealerClient.MessageListener {
  // it's important to use queuing here
  private val scopeDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
  private val scope = CoroutineScope(scopeDispatcher + SupervisorJob())

  private val writer = SpCollectionWriter(spSessionManager, internalApi, collectionApi, dbRepository, dao, scope)

  fun init() {
    spSessionManager.session.dealer().addMessageListener(this, "hm://collection/collection/" + spSessionManager.session.username(), "hm://collection/artist/" + spSessionManager.session.username())
    //spSessionManager.session.dealer().addMessageListener(this, "hm://playlist/v2/user/${spSessionManager.session.username()}/rootlist")
  }

  // scans network collection for tracks, albums, pins and artists
  suspend fun scan() = withContext(scopeDispatcher) {
    writer.performScan("collection")
    writer.performScan("artist")
    // performScan("ylpin")
  }

  suspend fun artists() = withContext(scopeDispatcher) {
    performScanIfEmpty("artist")
    dao.getArtists()
  }

  suspend fun albums() = withContext(scopeDispatcher) {
    performScanIfEmpty("collection")
    dao.getAlbums()
  }

  suspend fun tracksByArtist(id: String) = withContext(scopeDispatcher) {
    performScanIfEmpty("collection")
    dao.getTracksByArtist(id)
  }

  private suspend fun performScanIfEmpty(of: String) {
    Log.d("SpColManager", "Performing scan of $of (if empty)")
    dao.getCollection(of) ?: writer.performScan(of)
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