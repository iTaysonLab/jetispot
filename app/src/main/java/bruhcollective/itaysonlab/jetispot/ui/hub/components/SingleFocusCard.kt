package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext

@Composable
fun SingleFocusCard(
    item: HubItem
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(
                3.dp
            )
        ), modifier = Modifier
        .height(120.dp)
        .fillMaxWidth()
        .clickableHub(item)
    ) {
        Row {
            PreviewableAsyncImage(
                imageUrl = item.images?.main?.uri,
                placeholderType = item.images?.main?.placeholder,
                modifier = Modifier
                  .fillMaxHeight()
                  .width(120.dp)
            )
            Box(
              Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                Column(Modifier.align(Alignment.TopStart)) {
                    MediumText(text = item.text!!.title!!)
                    Subtext(text = item.text.subtitle!!)
                }
            }
        }
    }
}