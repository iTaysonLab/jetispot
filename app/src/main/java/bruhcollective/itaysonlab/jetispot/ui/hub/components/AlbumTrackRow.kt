package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext

@Composable
fun AlbumTrackRow(
    item: HubItem
) {
    Column(
      Modifier
        .clickableHub(item)
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
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