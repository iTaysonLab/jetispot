package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun PlaylistHeader(
    navController: NavController,
    delegate: HubScreenDelegate,
    item: HubItem
) {
    val darkTheme = isSystemInDarkTheme()
    val dominantColor = remember { mutableStateOf(Color.Transparent) }
    val dominantColorAsBg = animateColorAsState(dominantColor.value)

    LaunchedEffect(Unit) {
        launch {
            if (dominantColor.value != Color.Transparent) return@launch
            dominantColor.value =
                delegate.calculateDominantColor(item.images?.main?.uri.toString(), darkTheme)
        }
    }

    Column(
        Modifier
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(dominantColorAsBg.value, Color.Transparent)
                )
            )
            .padding(top = 16.dp)
            .statusBarsPadding()
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = item.images?.main?.uri),
            contentDescription = null,
            modifier = Modifier
                .size((LocalConfiguration.current.screenWidthDp * 0.7).dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        )
        Subtext(
            text = item.text?.subtitle!!, modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        )
    }
}