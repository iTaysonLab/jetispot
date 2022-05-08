package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.CollectionModel
import bruhcollective.itaysonlab.jetispot.ui.screens.dac.DacViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun YourLibraryRenderer(
  navController: NavController,
  source: YourLibrarySource<*>,
  viewModel: YourLibraryRendererViewModel = hiltViewModel()
) {
  LaunchedEffect(Unit) {
    launch {
      viewModel.load(source)
    }
  }

  source.render(navController)
}

@HiltViewModel
class YourLibraryRendererViewModel @Inject constructor(
  private val collectionManager: SpCollectionManager
): ViewModel() {
  suspend fun load(source: YourLibrarySource<*>) {
    source.load(collectionManager)
  }
}