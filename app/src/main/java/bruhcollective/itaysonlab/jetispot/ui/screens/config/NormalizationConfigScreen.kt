package bruhcollective.itaysonlab.jetispot.ui.screens.config

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.proto.AppConfig
import bruhcollective.itaysonlab.jetispot.proto.AudioNormalization
import bruhcollective.itaysonlab.jetispot.proto.AudioQuality
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpConfigurationManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NormalizationConfigScreen(
  navController: NavController,
  viewModel: NormalizationConfigScreenViewModel = hiltViewModel()
) {
  BaseConfigScreen(navController, viewModel)
}

@HiltViewModel
class NormalizationConfigScreenViewModel @Inject constructor(
  private val spConfigurationManager: SpConfigurationManager
) : ViewModel(), ConfigViewModel {
  private val configList = buildList {
    add(ConfigItem.LargeSwitch(R.string.enable, {
      it.playerConfig.normalization
    }, { value ->
      playerConfig = playerConfig.toBuilder().setNormalization(value).build()
    }))

    add(ConfigItem.Radio(R.string.normalization_loud, R.string.normalization_loud_desc, {
      it.playerConfig.normalizationLevel == AudioNormalization.LOUD
    }, { it.playerConfig.normalization }, {
      playerConfig = playerConfig.toBuilder().setNormalizationLevel(AudioNormalization.LOUD).build()
    }))

    add(ConfigItem.Radio(R.string.normalization_balanced, R.string.normalization_balanced_desc, {
      it.playerConfig.normalizationLevel == AudioNormalization.BALANCED
    }, { it.playerConfig.normalization }, {
      playerConfig = playerConfig.toBuilder().setNormalizationLevel(AudioNormalization.BALANCED).build()
    }))

    add(ConfigItem.Radio(R.string.normalization_quiet, R.string.normalization_quiet_desc, {
      it.playerConfig.normalizationLevel == AudioNormalization.QUIET
    }, { it.playerConfig.normalization }, {
      playerConfig = playerConfig.toBuilder().setNormalizationLevel(AudioNormalization.QUIET).build()
    }))

    add(ConfigItem.Info(R.string.warn_normalization))
  }

  override fun provideTitle() = R.string.config_normalization
  override fun provideDataStore() = spConfigurationManager.dataStore
  override fun provideConfigList() = configList
  override suspend fun modifyDatastore (runOnBuilder: AppConfig.Builder.() -> Unit) {
    spConfigurationManager.dataStore.updateData {
      it.toBuilder().apply(runOnBuilder).build()
    }
  }
}