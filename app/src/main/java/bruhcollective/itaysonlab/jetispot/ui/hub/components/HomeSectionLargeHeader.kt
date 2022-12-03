package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.SubtextOverline

@Composable
fun HomeSectionLargeHeader(
    item: HubItem
) {
    Row(
      Modifier
        .padding(vertical = 8.dp)
        .clickableHub(item)) {
        PreviewableAsyncImage(
            imageUrl = item.images?.main?.uri,
            placeholderType = item.images?.main?.placeholder,
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
        )

        Column(
          Modifier
            .padding(horizontal = 12.dp)
            .align(Alignment.CenterVertically)) {
            SubtextOverline(item.text!!.subtitle!!.uppercase(), modifier = Modifier)
            MediumText(item.text.title!!, modifier = Modifier.padding(top = 2.dp), fontSize = 21.sp)
        }
    }
}