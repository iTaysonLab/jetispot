package bruhcollective.itaysonlab.jetispot.core.metadata_db

import android.content.Context
import com.google.protobuf.ByteString
import com.tencent.mmkv.MMKV
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpMetadataDb @Inject constructor(
  @ApplicationContext private val context: Context
) {
  private val instance = MMKV.mmkvWithID("metadata")

  fun contains(uri: String) = instance.containsKey(uri)

  fun get(uri: String): ByteArray = instance.getBytes(uri, null)!!
  fun put(uri: String, msg: ByteArray) = instance.encode(uri, msg)

  fun clear() = instance.clearAll()
}