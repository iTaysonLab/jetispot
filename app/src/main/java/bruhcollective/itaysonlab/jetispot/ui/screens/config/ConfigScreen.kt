package bruhcollective.itaysonlab.jetispot.ui.screens.config

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
  navController: NavController,
  viewModel: ConfigScreenViewModel = hiltViewModel()
) {
  BaseConfigScreen(navController, viewModel)
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
          AudioQuality.NORMAL -> R.string.quality_normal
          AudioQuality.HIGH -> R.string.quality_high
          else -> R.string.quality_very_high
        }
      )
    }, { it.navigate("config/playbackQuality") }))

    add(ConfigItem.Preference(R.string.config_normalization, { ctx, cfg ->
      if (!cfg.playerConfig.normalization) return@Preference ctx.getString(R.string.normalization_disabled)
      ctx.getString(
        when (cfg.playerConfig.normalizationLevel) {
          AudioNormalization.BALANCED -> R.string.normalization_balanced
          AudioNormalization.QUIET -> R.string.normalization_quiet
          else -> R.string.normalization_loud
        }
      )
    }, { it.navigate("config/playbackNormalization") }))

    add(ConfigItem.Preference(R.string.config_crossfade, { ctx, cfg ->
      when (cfg.playerConfig.crossfade) {
        0 -> ctx.getString(R.string.crossfade_disabled)
        else -> ctx.getString(
          R.string.crossfade_enabled,
          cfg.playerConfig.crossfade
        ) // TODO: move to plurals
      }
    }, { it.navigate("config/playbackCrossfade") }))

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

    add(ConfigItem.Category(R.string.config_account))

    add(ConfigItem.Preference(R.string.config_logout, { ctx, cfg ->
      ctx.getString(
        R.string.config_logout_as, spSessionManager.session.username()
      )
    }, {
      // TODO
    }))

    add(ConfigItem.Category(R.string.config_about))

    add(ConfigItem.Preference(R.string.app_name, { ctx, _ ->
      ctx.getString(
        R.string.about_version, BuildConfig.VERSION_NAME
      )
    }, {}))

    add(ConfigItem.Preference(R.string.about_sources, { ctx, _ -> "" }, {
      openLink(it.context, "https://github.com/itaysonlab/jetispot")
    }))

    add(ConfigItem.Preference(R.string.about_channel, { ctx, _ -> "" }, {
      openLink(it.context, "https://t.me/bruhcollective")
    }))
  }

  override fun isRoot() = true
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