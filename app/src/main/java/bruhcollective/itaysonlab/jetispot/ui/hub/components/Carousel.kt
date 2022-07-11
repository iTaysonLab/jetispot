package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.hub.HubBinder
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Carousel(
  navController: LambdaNavigationController,
  delegate: HubScreenDelegate,
  item: HubItem,
) {
  Column(Modifier.padding(vertical = if (delegate.isSurroundedWithPadding()) 0.dp else 8.dp)) {
    if (item.text != null) {
      Text(
        text = item.text.title!!,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        modifier = Modifier
          .padding(horizontal = if (delegate.isSurroundedWithPadding()) 0.dp else 16.dp)
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
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          contentPadding = PaddingValues(16.dp)
        ) {
          items(item.children ?: listOf()) { cItem ->
            HubBinder(navController, delegate, cItem)
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
    }
  }
}