package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
fun PlaylistTrackRowLarger(
    item: HubItem
) {
    val artists = remember(item) {
        if (!item.text?.subtitle.isNullOrEmpty()) {
            item.text!!.subtitle!!
        } else if (item.custom?.get("artists") != null) {
            (item.custom["artists"] as List<Map<String, Any>>).joinToString { it["name"].toString() }
        } else {
            ""
        }
    }

    Row(
        Modifier
            .clickableHub(item)
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        PreviewableAsyncImage(
            imageUrl = item.images?.main?.uri,
            placeholderType = "track",
            modifier = Modifier
                .align(
                    Alignment.CenterVertically
                )
                .size(72.dp)
        )

        Column(
            Modifier
                .padding(
                    start = 16.dp
                )
                .align(Alignment.CenterVertically)
        ) {
            var drawnTitle = false

            if (!item.text?.title.isNullOrEmpty()) {
                drawnTitle = true
                MediumText(item.text!!.title!!, fontWeight = FontWeight.Normal)
            }

            Subtext(
                artists,
                modifier = Modifier.padding(top = if (drawnTitle) 4.dp else 8.dp),
                maxLines = 1,
            )
        }
    }
}