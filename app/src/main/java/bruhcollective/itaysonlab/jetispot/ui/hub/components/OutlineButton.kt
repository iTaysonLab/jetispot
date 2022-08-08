package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.clickableHub

@OptIn(ExperimentalTextApi::class)
@Composable
fun OutlineButton(item: HubItem) {
  Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
    Box(
      Modifier
        .height(40.dp)
        .clip(RoundedCornerShape(64.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .clickableHub(item),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = item.text?.title!!,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.padding(horizontal = 24.dp)
      )
    }
  }
}