package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.rememberLazyListSnapperLayoutInfo
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSnapperApi::class)
@Composable
fun Carousel(
  item: HubItem,
) {
  val lazyListState = rememberLazyListState()
  val lazySnapperLayoutInfo = rememberLazyListSnapperLayoutInfo(lazyListState)

  val isSurroundedWithPadding = LocalHubScreenDelegate.current.isSurroundedWithPadding()

  Column(Modifier.padding(vertical = if (isSurroundedWithPadding) 0.dp else 8.dp)) {
    if (item.text != null) {
      Text(
        text = item.text.title!!,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        modifier = Modifier
          .padding(horizontal = if (isSurroundedWithPadding) 0.dp else 16.dp)
          .padding(top = 8.dp, bottom = 12.dp)
      )
    }

    Box(
      Modifier
        .wrapContentSize()
        .padding(horizontal = 16.dp)
        .clip(shape = RoundedCornerShape(34.dp))
    ) {
      Card(
        shape = RoundedCornerShape(34.dp)
      ){
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(16.dp),
          state = lazyListState,
          flingBehavior = rememberSnapperFlingBehavior(
            lazyListState,
            snapOffsetForItem = SnapOffsets.Start,
            springAnimationSpec = spring(dampingRatio = 0.001f, stiffness = 10f)
          )
        ) {
          items(item.children ?: listOf()) { cItem ->
            HubBinder(cItem)
          }
        }
      }

      Spacer(
        Modifier
          .fillMaxWidth(0.04f)
          .height(274.dp)
          .background(
            brush = Brush.horizontalGradient(
              colors = listOf(
                Color.Transparent,
                MaterialTheme.colorScheme.surfaceVariant
              )
            )
          )
          .align(Alignment.BottomEnd)
      )

      Spacer(
        Modifier
          .fillMaxWidth(0.04f)
          .height(274.dp)
          .background(
            brush = Brush.horizontalGradient(
              colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                Color.Transparent
              )
            )
          )
      )
    }
  }
}