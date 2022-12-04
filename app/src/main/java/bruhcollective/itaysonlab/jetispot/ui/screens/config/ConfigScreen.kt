package bruhcollective.itaysonlab.jetispot.ui.screens.config

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.BuildConfig
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpConfigurationManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.proto.AppConfig
import bruhcollective.itaysonlab.jetispot.proto.AudioNormalization
import bruhcollective.itaysonlab.jetispot.proto.AudioQuality
import bruhcollective.itaysonlab.jetispot.ui.screens.Dialog
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun ConfigScreen(
  viewModel: ConfigScreenViewModel = hiltViewModel()
) {
  BaseConfigScreen(viewModel)
}

@HiltViewModel
class ConfigScreenViewModel @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val spConfigurationManager: SpConfigurationManager
) : ViewModel(), ConfigViewModel {
  private val configList = buildList {
    add(ConfigItem.Category(R.string.config_playback))

    add(ConfigItem.Preference(R.string.config_pbquality, { ctx, cfg ->
      ctx.getString(
        when (cfg.playerConfig.preferredQuality) {
          AudioQuality.LOW -> R.string.quality_low
          AudioQuality.NORMAL -> R.string.quality_normal
          AudioQuality.HIGH -> R.string.quality_high
          else -> R.string.quality_very_high
        }
      )
    }, { it.navigate(Screen.QualityConfig) }))

    add(ConfigItem.Preference(R.string.config_normalization, { ctx, cfg ->
      if (!cfg.playerConfig.normalization) return@Preference ctx.getString(R.string.normalization_disabled)
      ctx.getString(
        when (cfg.playerConfig.normalizationLevel) {
          AudioNormalization.BALANCED -> R.string.normalization_balanced
          AudioNormalization.QUIET -> R.string.normalization_quiet
          else -> R.string.normalization_loud
        }
      )
    }, { it.navigate(Screen.NormalizationConfig) }))

    add(ConfigItem.Slider(R.string.config_crossfade, { ctx, value ->
      when (value) {
        0 -> ctx.getString(R.string.crossfade_disabled)
        else -> ctx.resources.getQuantityString(R.plurals.seconds, value, value)
      }
    }, 0f..12f, 11, { cfg ->
      cfg.playerConfig.crossfade
    }, { num ->
      playerConfig = playerConfig.toBuilder().setCrossfade(num).build()
    }))

    add(
      ConfigItem.Switch(
        R.string.config_autoplay,
        R.string.config_autoplay_desc,
        { cfg -> cfg.playerConfig.autoplay },
        { value ->
          playerConfig = playerConfig.toBuilder().setAutoplay(value).build()
        })
    )

    add(ConfigItem.Info(R.string.warn_restart))

    add(ConfigItem.Category(R.string.config_device))

    add(ConfigItem.Preference(R.string.storage, { ctx, cfg -> "" }, {
      it.navigate(Screen.StorageConfig)
    }))
    add(ConfigItem.Preference(R.string.language, { ctx, cfg -> "" }, {
      it.navigate(Screen.LanguageConfig)
    }))

    add(ConfigItem.Category(R.string.config_account))

    add(ConfigItem.Preference(R.string.config_logout, { ctx, cfg ->
      ctx.getString(
        R.string.config_logout_as, spSessionManager.session.username()
      )
    }, {
      it.navigate(Dialog.Logout)
    }))

    add(ConfigItem.Preference(R.string.config_viewplan, { ctx, cfg ->
      ctx.getString(R.string.config_viewplan_desc)
    }, {
      it.navigate(Screen.DacViewCurrentPlan)
    }))

    add(ConfigItem.Category(R.string.config_about))

    add(ConfigItem.Preference(R.string.app_name, { ctx, _ ->
      ctx.getString(
        R.string.about_version, BuildConfig.VERSION_NAME
      )
    }, {}))

    add(ConfigItem.Preference(R.string.about_sources, { ctx, _ -> "" }, {
      it.openInBrowser("https://github.com/BobbyESP/Jetispot")
    }))

    /*add(ConfigItem.Preference(R.string.about_channel, { ctx, _ -> "" }, {
      it.openInBrowser("https://t.me/bruhcollective")
    }))*/
  }

  override fun isRoot() = false
  override fun provideTitle() = R.string.config_root
  override fun provideDataStore() = spConfigurationManager.dataStore
  override fun provideConfigList() = configList
  override suspend fun modifyDatastore (runOnBuilder: AppConfig.Builder.() -> Unit) {
    spConfigurationManager.dataStore.updateData {
      it.toBuilder().apply(runOnBuilder).build()
    }
  }

  private fun openLink(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
  }
}