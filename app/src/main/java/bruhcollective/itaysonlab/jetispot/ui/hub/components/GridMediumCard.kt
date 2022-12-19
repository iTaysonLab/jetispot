package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext

@Composable
fun GridMediumCard(
    item: HubItem
) {
    val size = 160.dp

    Column(
        Modifier
            .fillMaxWidth()
            .clickableHub(item)
    ) {
        var drawnTitle = false

        PreviewableAsyncImage(
            imageUrl = item.images?.main?.uri,
            placeholderType = item.images?.main?.placeholder,
            modifier = Modifier
                .size(size)
                .clip(
                    RoundedCornerShape(if (item.images?.main?.isRounded == true) 12.dp else 0.dp)
                )
                .align(Alignment.CenterHorizontally)
        )

        if (!item.text?.title.isNullOrEmpty()) {
            drawnTitle = true
            MediumText(
                item.text!!.title!!,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp)
            )
        }

        if (!item.text?.subtitle.isNullOrEmpty()) {
            Subtext(
                item.text!!.subtitle!!,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (drawnTitle) 2.dp else 8.dp, bottom = 12.dp)
                    .padding(horizontal = 16.dp)
            )
        } else if (!item.text?.description.isNullOrEmpty()) {
            Subtext(
                item.text!!.description!!,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (drawnTitle) 2.dp else 8.dp, bottom = 12.dp)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}