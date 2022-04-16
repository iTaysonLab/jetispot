package bruhcollective.itaysonlab.jetispot.core

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import bruhcollectie.itaysonlab.jetispot.proto.AppConfig
import bruhcollectie.itaysonlab.jetispot.proto.AudioNormalization
import bruhcollectie.itaysonlab.jetispot.proto.AudioQuality
import bruhcollectie.itaysonlab.jetispot.proto.PlayerConfig
import com.google.protobuf.InvalidProtocolBufferException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpConfigurationManager @Inject constructor(
  @ApplicationContext private val appContext: Context
) {
  private val defaultConfig = AppConfig.newBuilder().apply {
    setPlayerConfig(PlayerConfig.newBuilder().apply {
      autoplay = true
      normalization = true
      preferredQuality = AudioQuality.VERY_HIGH
      normalizationLevel = AudioNormalization.BALANCED
      crossfade = 0
      preload = true
    })
  }.build()

  private val dataStore = DataStoreFactory.create(object: Serializer<AppConfig> {
    override val defaultValue = defaultConfig
    override suspend fun writeTo(t: AppConfig, output: OutputStream) = t.writeTo(output)

    override suspend fun readFrom(input: InputStream) = try {
      AppConfig.parseFrom(input)
    } catch (e: InvalidProtocolBufferException) {
      throw CorruptionException("proto parsing failed")
    }
  }) { File(appContext.filesDir, "spa_prefs") }

  fun syncPlayerConfig(): PlayerConfig = runBlocking { dataStore.data.first().playerConfig }
}