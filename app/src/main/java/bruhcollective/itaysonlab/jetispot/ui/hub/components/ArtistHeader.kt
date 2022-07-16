package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.LargeTopAppBar

@Composable
fun ArtistHeader(
    item: HubItem,
    scrollBehavior: TopAppBarScrollBehavior,
    navController: LambdaNavigationController
) {
    LargeTopAppBar(
        title = { Text(item.text?.title!!) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
            }
        },
        contentPadding = PaddingValues(
            top = with(LocalDensity.current) {
                WindowInsets.statusBars.getTop(LocalDensity.current).toDp()
            }
        )
    )

//    Column(
//        modifier = Modifier
//            .size((LocalConfiguration.current.screenWidthDp).dp, 310.dp),
//    ) {
//        Box {
//            AsyncImage(
//                model = item.images?.main?.uri, contentDescription = null,
//                Modifier.fillMaxWidth(),
//                contentScale = ContentScale.FillWidth
//            )
//
//            Column(
//                Modifier
//                    .background(
//                        Brush.verticalGradient(
//                            0F to Color.Transparent,
//                            1F to Color.Black,
//                        ),
//                    )
//                    .fillMaxSize()
//            ) {}
//
//            MediumText(
//                text = item.text?.title!!,
//                fontSize = 50.sp,
//                color = Color.White,
//                modifier = Modifier
//                    .align(Alignment.BottomStart)
//                    .padding(horizontal = 16.dp)
//                    .padding(bottom = 8.dp)
//            )
//        }
//    }
}
