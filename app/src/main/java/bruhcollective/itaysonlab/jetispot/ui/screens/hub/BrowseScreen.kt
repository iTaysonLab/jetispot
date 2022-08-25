package bruhcollective.itaysonlab.jetispot.ui.screens.hub

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
  id: String
) {
  val navController = LocalNavigationController.current

  val scrollBehavior = rememberEUCScrollBehavior()
  var appBarTitle by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      LargeTopAppBar(
        title = {
          Text(appBarTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = {
          IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Rounded.ArrowBack, null)
          }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(),
        scrollBehavior = scrollBehavior
      )
    },
    modifier = Modifier
      .fillMaxSize()
      .nestedScroll(scrollBehavior.nestedScrollConnection)
  ) { padding ->
    Box(Modifier.padding(padding)) {
      HubScreen(
        loader = { getBrowseView(id) },
        onAppBarTitleChange = { appBarTitle = it }
      )
    }
  }
}