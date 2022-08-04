package bruhcollective.itaysonlab.jetispot.ui.ext

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberEUCScrollBehavior(): TopAppBarScrollBehavior {
  val sbd = rememberSplineBasedDecay<Float>()
  val state = rememberTopAppBarState()
  return remember { TopAppBarDefaults.exitUntilCollapsedScrollBehavior(sbd, state) }
}