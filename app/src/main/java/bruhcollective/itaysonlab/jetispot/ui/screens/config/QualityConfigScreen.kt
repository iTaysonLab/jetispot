package bruhcollective.itaysonlab.jetispot.ui.screens.config

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollectie.itaysonlab.jetispot.proto.AppConfig
import bruhcollectie.itaysonlab.jetispot.proto.AudioNormalization
import bruhcollectie.itaysonlab.jetispot.proto.AudioQuality
import bruhcollective.itaysonlab.jetispot.BuildConfig
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpConfigurationManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityConfigScreen(
  navController: NavController,
  viewModel: QualityConfigScreenViewModel = hiltViewModel()
) {
  BaseConfigScreen(navController, viewModel)
}

@HiltViewModel
class QualityConfigScreenViewModel @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val spConfigurationManager: SpConfigurationManager
) : ViewModel(), ConfigViewModel {
  private val configList = buildList {
    add(ConfigItem.Radio(R.string.quality_normal, R.string.quality_normal_desc, {
      it.playerConfig.preferredQuality == AudioQuality.NORMAL
    }, {
      playerConfig = playerConfig.toBuilder().setPreferredQuality(AudioQuality.NORMAL).build()
    }))

    add(ConfigItem.Radio(R.string.quality_high, R.string.quality_high_desc, {
      it.playerConfig.preferredQuality == AudioQuality.HIGH
    }, {
      playerConfig = playerConfig.toBuilder().setPreferredQuality(AudioQuality.HIGH).build()
    }))

    add(ConfigItem.Radio(R.string.quality_very_high, R.string.quality_very_high_desc, {
      it.playerConfig.preferredQuality == AudioQuality.VERY_HIGH
    }, {
      playerConfig = playerConfig.toBuilder().setPreferredQuality(AudioQuality.VERY_HIGH).build()
    }))

    add(ConfigItem.Switch(R.string.config_tremolo, R.string.config_tremolo_desc, {
      it.playerConfig.useTremolo
    }, { value ->
      playerConfig = playerConfig.toBuilder().setUseTremolo(value).build()
    }))

    add(ConfigItem.Info(R.string.warn_quality))
  }

  override fun provideTitle() = R.string.config_pbquality
  override fun provideDataStore() = spConfigurationManager.dataStore
  override fun provideConfigList() = configList
  override suspend fun modifyDatastore (runOnBuilder: AppConfig.Builder.() -> Unit) {
    spConfigurationManager.dataStore.updateData {
      it.toBuilder().apply(runOnBuilder).build()
    }
  }
}