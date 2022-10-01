package bruhcollective.itaysonlab.jetispot.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.objs.player.PfcContextData
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextPlayerData
import bruhcollective.itaysonlab.jetispot.core.util.playCommand
import bruhcollective.itaysonlab.jetispot.proto.SearchViewResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val spInternalApi: SpInternalApi,
    private val spPlayerServiceManager: SpPlayerServiceManager
): ViewModel(), CoroutineScope by MainScope() {
    var searchQuery by mutableStateOf(TextFieldValue())

    var searchResponse by mutableStateOf<SearchViewResponse?>(null)
        private set

    suspend fun initiateSearch() {
        searchResponse = null
        searchResponse = spInternalApi.search(
            query = searchQuery.text
        )
    }

    fun dispatchPlay(uri: String) {
        spPlayerServiceManager.play(
            playCommand(uri) {
                contextUri = "spotify:search:${searchQuery.text.replace(" ", "+")}"
            }
        )
    }

    fun clear() {
        searchQuery = TextFieldValue()
        searchResponse = null
    }
}