package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage

@Composable
fun ImageRow(
    item: HubItem
) {
    Row(
      Modifier
        .clickableHub(item)
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)) {
        PreviewableAsyncImage(
            imageUrl = item.images?.main?.uri,
            placeholderType = item.images?.main?.placeholder,
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
        )

        Column(
          Modifier
            .padding(horizontal = 12.dp)
            .align(Alignment.CenterVertically)) {
            MediumText(item.text!!.title!!, fontWeight = FontWeight.Normal, fontSize = 18.sp)
        }
    }
}