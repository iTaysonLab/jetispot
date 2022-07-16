package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.ImageBackgroundTopAppBar
import coil.compose.AsyncImage

@Composable
fun ArtistHeader(
  item: HubItem,
  scrollBehavior: TopAppBarScrollBehavior,
  navController: LambdaNavigationController
) {
  ImageBackgroundTopAppBar(
    title = {
      Text(
        item.text!!.title!!,
        Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
        maxLines = 4
      )
    },
    smallTitle = {
      Text(
        item.text!!.title!!,
        Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    },
    picture = {
      AsyncImage(
        model = item.images?.main?.uri, contentDescription = null,
        Modifier
          .fillMaxWidth()
          .height(61.dp + WindowInsets.statusBars.getTop(LocalDensity.current).dp),
        contentScale = ContentScale.FillWidth
      )
    },
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
