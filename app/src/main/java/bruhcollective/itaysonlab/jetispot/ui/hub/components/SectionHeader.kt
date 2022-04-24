package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubText
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate

@Composable
fun SectionHeader(
    text: HubText,
    delegate: HubScreenDelegate
) {
    Box(
        Modifier
            .padding(
                vertical = 8.dp,
                horizontal = if (delegate.isSurroundedWithPadding()) 0.dp else 16.dp
            )
    ) {
        Text(
            text = text.title!!,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}