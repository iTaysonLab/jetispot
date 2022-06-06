package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary.debug

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourLibraryDebugScreen(
  navController: LambdaNavigationController,
  viewModel: YourLibraryDebugScreenViewModel = hiltViewModel()
) {
  val scrollBehavior = rememberEUCScrollBehavior()
  val scope = rememberCoroutineScope()

  val items = listOf(
    "Rescan (collection)" to { scope.launch { viewModel.rescan() }},
    "Rescan (rootlist)" to { },
    "Clear (everything)" to { scope.launch { viewModel.clean() }},
  )

  Scaffold(topBar = {
    bruhcollective.itaysonlab.jetispot.ui.shared.evo.LargeTopAppBar(title = {
      Text("Your Library: debugging")
    }, navigationIcon = {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Rounded.ArrowBack, null)
      }
    }, contentPadding = PaddingValues(top = with(LocalDensity.current) { WindowInsets.statusBars.getTop(
      LocalDensity.current).toDp() }), scrollBehavior = scrollBehavior)
  }, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) { padding ->
    LazyColumn(
      Modifier
        .fillMaxHeight()
        .padding(padding)) {
      items(items) { item ->
        Column(modifier = Modifier
          .fillMaxWidth()
          .clickable { item.second() }
          .padding(16.dp)) {
          Text(text = item.first, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp)
        }
      }
    }
  }
}

@HiltViewModel
class YourLibraryDebugScreenViewModel @Inject constructor(
  private val collectionManager: SpCollectionManager
): ViewModel() {
  suspend fun rescan() = collectionManager.scan()
  suspend fun clean() = collectionManager.clean()
}