package bruhcollective.itaysonlab.jetispot.ui.shared.evo

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayFAB(
  item: HubItem,
  scrollBehavior: TopAppBarScrollBehavior
) {
  val delegate = LocalHubScreenDelegate.current

  val fabSize = animateDpAsState(
    if (scrollBehavior.state.collapsedFraction <= 0.02f) 56.dp else 0.dp,
    animationSpec = tween(durationMillis = 500)
  ).value
  Box {
    item.children?.get(0)?.let {
        Modifier
          .clip(RoundedCornerShape(16.dp))
          .size(fabSize)
          .background(MaterialTheme.colorScheme.primaryContainer)
          .clickableHub(it)
    }?.let {
      Box(
        it
    ) {
        Icon(
          imageVector = Icons.Rounded.PlayArrow,
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
          contentDescription = null,
          modifier = Modifier
            .size(32.dp)
            .align(Alignment.Center)
        )
    }
    }

    if ((item.children?.get(0)?.events?.click as? HubEvent.PlayFromContext)?.data?.player?.options?.player_options_override?.shuffling_context != false) {
      Box(
        Modifier
          .align(Alignment.BottomEnd)
          .offset(4.dp, 4.dp)
          .clip(CircleShape)
          .size(fabSize * 0.35f)
          .background(MaterialTheme.colorScheme.compositeSurfaceElevation(4.dp))
      ) {
        Icon(
          imageVector = Icons.Rounded.Shuffle,
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
          contentDescription = null,
          modifier = Modifier
            .padding(4.dp)
            .align(Alignment.Center)
        )
      }
    }
  }
}