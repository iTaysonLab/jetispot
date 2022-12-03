package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext

@Composable
fun ArtistPinnedItem(
    item: HubItem
) {
    Row(
      Modifier
        .clickableHub(item)
        .padding(horizontal = 16.dp, vertical = 2.dp)
    ) {
        PreviewableAsyncImage(
            imageUrl = item.images?.main?.uri,
            placeholderType = item.images?.main?.placeholder,
            modifier = Modifier
              .size(72.dp)
              .padding(vertical = 8.dp)
              .padding(end = 16.dp)
        )
        Column(Modifier.align(Alignment.CenterVertically)) {
            MediumText(
                item.text!!.title!!,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Subtext(item.text.subtitle!!)
        }
    }
}