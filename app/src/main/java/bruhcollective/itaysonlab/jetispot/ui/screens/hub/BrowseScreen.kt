package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.LargeTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
  id: String
) {
  val navController = LocalNavigationController.current
  val scrollBehavior = rememberEUCScrollBehavior()
  var appBarTitle by remember { mutableStateOf("") }

  Scaffold(topBar = {
    LargeTopAppBar(title = {
      Text(appBarTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }, navigationIcon = {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Rounded.ArrowBack, null)
      }
    }, colors = TopAppBarDefaults.largeTopAppBarColors(), contentPadding = PaddingValues(top = with(LocalDensity.current) { WindowInsets.statusBars.getTop(LocalDensity.current).toDp() }), scrollBehavior = scrollBehavior)
  }, modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection)) { padding ->
    Box(Modifier.padding(padding)) {
      HubScreen(
        needContentPadding = false,
        loader = { getBrowseView(id) },
        onAppBarTitleChange = { appBarTitle = it }
      )
    }
  }
}