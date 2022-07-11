package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    Card(shape = RoundedCornerShape(32.dp), modifier = Modifier.padding(horizontal = 16.dp)) {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(22.dp)
      ) {
        items(item.children ?: listOf()) { cItem ->
          HubBinder(navController, delegate, cItem)
        }
      }
    }
  }
}