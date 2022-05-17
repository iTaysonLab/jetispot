package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.player.*
import bruhcollective.itaysonlab.jetispot.ui.hub.virt.CollectionEntityView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
  navController: NavController,
  viewModel: CollectionViewModel = hiltViewModel()
) {
  LaunchedEffect(Unit) {
    viewModel.load()
  }

  HubScaffold(
    navController = navController,
    appBarTitle = stringResource(id = R.string.liked_songs),
    state = viewModel.state,
    viewModel = viewModel
  ) {}
}

@HiltViewModel
class CollectionViewModel @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val spPlayerServiceManager: SpPlayerServiceManager,
  private val spCollectionManager: SpCollectionManager,
  private val spDao: LocalCollectionDao,
) : AbsHubViewModel() {
  private var requestedCollection = false
  private var currentTag: String? = null

  private var currentSort = LocalCollectionDao.TrackSorts.ByTime
  private var currentSortInvert = false

  suspend fun load() = load {
    if (!requestedCollection) {
      spCollectionManager.performCollectionScan()
      requestedCollection = true
    }

    CollectionEntityView.create(spSessionManager, spDao, currentSort, currentSortInvert, currentTag)
  }

  override fun play(data: PlayFromContextData) = play(spPlayerServiceManager, data)
  override suspend fun calculateDominantColor(url: String, dark: Boolean) = Color.Transparent

  override fun sendCustomCommand(scope: CoroutineScope, cmd: Any): Any {
    when (val pCmd = cmd as Command) {
      is Command.SetTag -> {
        currentTag = pCmd.query
        scope.launch(Dispatchers.Main) { load() }
      }

      Command.ClearTag -> {
        currentTag = null
        scope.launch(Dispatchers.Main) { load() }
      }

      is Command.SetSort -> {
        currentSort = pCmd.sort
        scope.launch(Dispatchers.Main) { load() }
      }

      Command.ToggleSortInvert -> {
        currentSortInvert = !currentSortInvert
        scope.launch(Dispatchers.Main) { load() }
      }

      Command.GetSort -> { return currentSort }
      Command.GetSortInvert -> { return currentSortInvert }

      Command.Play -> {
        val uri = "spotify:user:${spSessionManager.session.username()}:collection"
        play(
          PlayFromContextData(
            uri,
            PlayFromContextPlayerData(
              context = PfcContextData(url = "context://$uri", uri = uri),
              state = PfcState(PfcStateOptions(shuffling_context = true)),
              options = PfcOptions(player_options_override = PfcStateOptions(shuffling_context = true))
            )
          )
        )
      }
    }

    return Unit
  }

  sealed class Command {
    class SetTag(val query: String): Command()
    object ClearTag: Command()

    class SetSort(val sort: LocalCollectionDao.TrackSorts): Command()
    object GetSort: Command()

    object GetSortInvert: Command()
    object ToggleSortInvert: Command()

    object Play: Command()
  }
}