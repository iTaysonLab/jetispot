package bruhcollective.itaysonlab.jetispot.ui.hub.components.essentials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub

@Composable
fun EntityActionStrip(
    delegate: HubScreenDelegate,
    item: HubItem
) {
    Row(
      Modifier
        .padding(horizontal = 16.dp)
        .padding(bottom = 4.dp)) {
        IconButton(
            onClick = { delegate.toggleMainObjectAddedState() },
          Modifier
            .offset(y = 2.dp)
            .align(Alignment.CenterVertically)
            .size(28.dp)
        ) {
            Icon(
                if (delegate.getMainObjectAddedState().value) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                null
            )
        }

        Spacer(Modifier.width(16.dp))

        IconButton(
            onClick = { /*TODO*/ },
          Modifier
            .offset(y = 2.dp)
            .align(Alignment.CenterVertically)
            .size(28.dp)
        ) {
            Icon(Icons.Rounded.MoreVert, null)
        }

        Spacer(Modifier.weight(1f))

        Box(Modifier.size(48.dp)) {
            Box(
              Modifier
                .clip(CircleShape)
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary)
                .clickableHub(item.children!![0])
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null,
                    modifier = Modifier
                      .size(32.dp)
                      .align(Alignment.Center)
                )
            }

            if ((item.children[0].events?.click as? HubEvent.PlayFromContext)?.data?.player?.options?.player_options_override?.shuffling_context != false) {
                Box(
                  Modifier
                    .align(Alignment.BottomEnd)
                    .offset(4.dp, 4.dp)
                    .clip(CircleShape)
                    .size(22.dp)
                    .background(MaterialTheme.colorScheme.compositeSurfaceElevation(4.dp))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier
                          .padding(4.dp)
                          .align(Alignment.Center)
                    )
                }
            }
        }
    }
}