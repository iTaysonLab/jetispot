package bruhcollective.itaysonlab.jetispot.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.objs.player.*
import bruhcollective.itaysonlab.jetispot.core.util.Log
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
        Log.d("Dispatcher", "Dispatching play command for $uri")
        spPlayerServiceManager.play(
            PlayFromContextData(
                "spotify:search:${searchQuery.text.replace(" ", "+")}",
                PlayFromContextPlayerData(
                    context = PfcContextData(url = "context://$uri", uri = uri),
                    state = PfcState(PfcStateOptions(shuffling_context = false)),
                    options = PfcOptions(player_options_override = PfcStateOptions(shuffling_context = false) )
                )

        ))
    }

    fun clear() {
        searchQuery = TextFieldValue()
        searchResponse = null
    }
}