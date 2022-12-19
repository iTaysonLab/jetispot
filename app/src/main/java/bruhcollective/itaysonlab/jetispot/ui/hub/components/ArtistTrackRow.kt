package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
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
fun ArtistTrackRow(
    item: HubItem
) {
    Row(
      Modifier
        .clickableHub(item)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = (item.custom!!["rowNumber"] as Double).toInt().toString(), modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(end = 16.dp)
        )

        PreviewableAsyncImage(
            imageUrl = item.images?.main?.uri,
            placeholderType = item.images?.main?.placeholder,
            modifier = Modifier
              .align(Alignment.CenterVertically)
              .size(48.dp)
        )

        Column(
          Modifier
            .align(Alignment.CenterVertically)
            .padding(start = 16.dp)) {
            var drawnTitle = false

            if (!item.text?.title.isNullOrEmpty()) {
                drawnTitle = true
                MediumText(item.text!!.title!!, fontWeight = FontWeight.Normal)
            }

            if (!item.text?.subtitle.isNullOrEmpty()) {
                Subtext(
                    item.text!!.subtitle!!,
                    modifier = Modifier.padding(top = if (drawnTitle) 4.dp else 8.dp)
                )
            }
        }
    }
}