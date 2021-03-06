package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import coil.compose.AsyncImage

@Composable
fun ArtistHeader(
    item: HubItem
) {
    Column(
        modifier = Modifier
            .size((LocalConfiguration.current.screenWidthDp).dp, 310.dp),
    ) {
        Box {
            AsyncImage(
                model = item.images?.main?.uri, contentDescription = null,
                Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Column(
                Modifier
                    .background(
                        Brush.verticalGradient(
                            0F to Color.Transparent,
                            1F to Color.Black,
                        ),
                    )
                    .fillMaxSize()
            ) {}

            MediumText(
                text = item.text?.title!!,
                fontSize = 50.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            )
        }
    }
}
