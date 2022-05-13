package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpPartnersApi
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate

abstract class AbsHubViewModel: ViewModel(), HubScreenDelegate {
  private val _state = mutableStateOf<HubState>(HubState.Loading)
  val state: HubState get() = _state.value

  val mainAddedState = mutableStateOf(false)
  val imageCache = mutableMapOf<String, Color>()

  suspend fun load(loader: suspend () -> HubResponse) {
    _state.value = try {
      HubState.Loaded(loader())
    } catch (e: Exception) {
      e.printStackTrace()
      HubState.Error(e)
    }
  }

  suspend fun reload(loader: suspend () -> HubResponse) {
    _state.value = HubState.Loading
    load(loader)
  }

  fun play(spPlayerServiceManager: SpPlayerServiceManager, data: PlayFromContextData) {
    spPlayerServiceManager.play(data.uri, data.player)
  }

  override fun isSurroundedWithPadding() = false
  override fun getMainObjectAddedState() = mainAddedState

  suspend fun calculateDominantColor(partnersApi: SpPartnersApi, url: String, dark: Boolean): Color {
    return try {
      if (imageCache.containsKey(url)) {
        return imageCache[url]!!
      }

      val apiResult = partnersApi.fetchExtractedColors(variables = "{\"uris\":[\"$url\"]}").data.extractedColors[0].let {
        if (dark) it.colorRaw else it.colorDark
      }.hex

      Color(android.graphics.Color.parseColor(apiResult)).also { imageCache[url] = it }
    } catch (e: Exception) {
      // e.printStackTrace()
      Color.Transparent
    }
  }
}