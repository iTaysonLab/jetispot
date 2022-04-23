package bruhcollective.itaysonlab.jetispot.core

import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextPlayerData
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalStdlibApi::class)
@Singleton
class SpPlayerServiceManager @Inject constructor(
  @ApplicationContext private val context: Context,
  private val moshi: Moshi
) {
  private val impl = SpPlayerServiceImpl(context, this)

  // states
  val currentTrack = mutableStateOf<MediaItem?>(null)

  fun reset() {
    currentTrack.value = null
  }
  //

  // Uri should be spotify:<track/album/..>:<id>
  fun play (uri: String, data: PlayFromContextPlayerData) = impl.awaitService {
    setMediaUri(uri.toUri(), Bundle().also { it.putString("sp_json", moshi.adapter<PlayFromContextPlayerData>().toJson(data)) })
  }
}