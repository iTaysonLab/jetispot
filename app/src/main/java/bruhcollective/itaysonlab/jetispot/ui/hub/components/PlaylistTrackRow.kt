package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import coil.compose.AsyncImage

@Composable
fun PlaylistTrackRow(
    navController: NavController,
    delegate: HubScreenDelegate,
    item: HubItem
) {
    Row(
        Modifier
            .clickableHub(navController, delegate, item)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        AsyncImage(
            model = item.images!!.main!!.uri,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .align(
                    Alignment.CenterVertically
                )
                .padding(
                    end = 16.dp
                )
                .size(48.dp)
        )

        Column(Modifier.align(Alignment.CenterVertically)) {
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