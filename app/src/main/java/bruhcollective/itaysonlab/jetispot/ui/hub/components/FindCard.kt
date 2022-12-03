package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage

@Composable
fun FindCard(
    item: HubItem
) {
    Card(modifier = Modifier
      .height(100.dp)
      .fillMaxWidth()
      .clickableHub(item)) {
        Box {
            PreviewableAsyncImage(
                imageUrl = item.images?.background?.uri,
                placeholderType = item.images?.background?.placeholder,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                item.text!!.title!!,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                  .align(Alignment.TopStart)
                  .padding(12.dp)
            )
        }
    }
}