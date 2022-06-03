package bruhcollective.itaysonlab.jetispot.ui.screens.blend

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpConfigurationManager
import bruhcollective.itaysonlab.jetispot.core.api.SpBlendApi
import bruhcollective.itaysonlab.jetispot.proto.AppConfig
import bruhcollective.itaysonlab.jetispot.ui.ext.shareUrl
import bruhcollective.itaysonlab.jetispot.ui.screens.config.BaseConfigScreen
import bruhcollective.itaysonlab.jetispot.ui.screens.config.ConfigItem
import bruhcollective.itaysonlab.jetispot.ui.screens.config.ConfigViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@Composable
fun BlendCreateInvitationScreen (
  navController: LambdaNavigationController,
  viewModel: BlendCreateInvitationViewModel = hiltViewModel()
) {
  BaseConfigScreen(navController, viewModel)
}

@HiltViewModel
class BlendCreateInvitationViewModel @Inject constructor(
  private val spBlendApi: SpBlendApi
) : ViewModel(), ConfigViewModel, CoroutineScope by MainScope() {
  override suspend fun modifyDatastore(runOnBuilder: AppConfig.Builder.() -> Unit) {}
  override fun provideDataStore() = SpConfigurationManager.EMPTY
  override fun provideTitle() = R.string.blend_create

  override fun provideConfigList() = buildList {
    add(ConfigItem.Preference(
      R.string.blend_create_btn, { _, _ -> "" }
    ) {
      launch {
        val link = withContext(Dispatchers.IO) { spBlendApi.generateBlend().invite }
        it.context().shareUrl(link, it.string(R.string.blend_invite))
      }
    })

    add(ConfigItem.Info(R.string.blend_info))
  }
}