package bruhcollective.itaysonlab.jetispot.ui.blocks

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import xyz.gianlu.librespot.metadata.ImageId

@Composable
fun TwoColumnAndImageBlock(
    artworkUri: String? = "spotify:image:ab67706c0000bebb8d0ce13d55f634e290f744ba",
    title: String,
    text: String,
    modifier: Modifier = Modifier
) {

    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (artworkUri!!.startsWith("spotify:image")) {
            val imageId = remember { artworkUri }
            PreviewableAsyncImage(
                imageUrl = remember(artworkUri) {
                    artworkUri.let {
                        "https://i.scdn.co/image/" + ImageId.fromUri(
                            imageId
                        ).hexId()
                    }
                }, placeholderType = "track", modifier = Modifier
                    .size(48.dp)
            )
        } else {
            PreviewableAsyncImage(
                imageUrl = artworkUri,
                modifier = Modifier
                    .size(48.dp),
                placeholderType = "track"
            )
        }

        Column(
            Modifier
                .padding(horizontal = 12.dp)
                .align(Alignment.CenterVertically)
        ) {
            MediumText(title, fontWeight = FontWeight.Normal, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Subtext(text, modifier = Modifier, maxLines = 1)
        }
    }
}