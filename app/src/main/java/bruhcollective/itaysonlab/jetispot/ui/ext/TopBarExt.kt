package bruhcollective.itaysonlab.jetispot.ui.ext

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberEUCScrollBehavior(): TopAppBarScrollBehavior {
  val state = rememberTopAppBarState()
  return TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state)
}