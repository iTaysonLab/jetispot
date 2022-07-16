package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubEvent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.core.objs.hub.NavigateUri
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.hub.HubEventHandler
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.LargeImageTopAppBar
import coil.compose.AsyncImage


@Composable
fun AlbumHeader(
  navController: LambdaNavigationController,
  delegate: HubScreenDelegate,
  item: HubItem,
  scrollBehavior: TopAppBarScrollBehavior
) {
  // TODO: discard dominant color calculation for album header?
//  val darkTheme = isSystemInDarkTheme()
//  val dominantColor = remember { mutableStateOf(Color.Transparent) }
//  val dominantColorAsBg = animateColorAsState(dominantColor.value)
//
//  LaunchedEffect(Unit) {
//    launch {
//      if (dominantColor.value != Color.Transparent) return@launch
//      dominantColor.value = delegate.calculateDominantColor(item.images?.main?.uri.toString(), darkTheme)
//    }
//  }

  LargeImageTopAppBar(
    navigationIcon = {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
      }
    },
    actions = {
      IconButton(onClick = { /*TODO*/ }) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Options for ${item.text!!.title!!} by ${item.text!!.subtitle!!}",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }
    },
    scrollBehavior = scrollBehavior,
    contentPadding = PaddingValues(
      top = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(LocalDensity.current).toDp()
      }
    ),
    artwork = {
      PreviewableAsyncImage(
        item.images?.main?.uri, item.images?.main?.placeholder,
        modifier = Modifier
          .fillMaxSize()
      )
    },
    title = {
      Text(
        item.text!!.title!!,
        Modifier.fillMaxWidth(0.5f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 4
      )
    },
    smallTitle = {
      Text(
        item.text!!.title!!,
        Modifier.fillMaxWidth(0.8f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    },
    artist = {
      Row(
        modifier = Modifier
          .padding(start = 4.dp)
          .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
            HubEventHandler.handle(
              navController,
              delegate,
              HubEvent.NavigateToUri(NavigateUri(item.metadata?.album!!.artists[0].uri!!))
           )
        }
        .padding(vertical = 12.dp)
      ) {
        AsyncImage(
          model = item.metadata?.album!!.artists.first().images!![0].uri,
          contentScale = ContentScale.Crop,
          contentDescription = null,
          modifier = Modifier.size(32.dp).clip(CircleShape)
        )

        Text(
          text = item.metadata.album.artists.first().name!!,
          fontSize = 14.sp,
          modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp)
        )
      }
    }
  )
//  LargeTopAppBar(
//    title = {
//      Row(
//        Modifier.fillMaxSize(),
//        horizontalArrangement = Arrangement.SpaceBetween,
//      ) {
//        Column() {
//          Row(modifier = Modifier
//            .clickable(
//              interactionSource = remember { MutableInteractionSource() },
//              indication = null
//            ) {
//              HubEventHandler.handle(
//                navController,
//                delegate,
//                HubEvent.NavigateToUri(NavigateUri(item.metadata?.album!!.artists[0].uri!!))
//              )
//            }
//            .padding(horizontal = 0.dp)
//            .padding(vertical = 12.dp)) {
//            AsyncImage(model = item.metadata?.album!!.artists.first().images!![0].uri, contentScale = ContentScale.Crop, contentDescription = null, modifier = Modifier
//              .clip(CircleShape)
//              .size(32.dp)
//              .clearAndSetSemantics { }
//            )
//            MediumText(text = item.metadata.album.artists.first().name!!, fontSize = 13.sp, modifier = Modifier
//              .align(Alignment.CenterVertically)
//              .padding(start = 12.dp))
//          }
//          Text(item.text!!.title!!)
//        }
//
//        Row() {
//          Column() {
//
//          }
//          PreviewableAsyncImage(
//            item.images?.main?.uri, item.images?.main?.placeholder,
//            modifier = Modifier
//              .size(((LocalConfiguration.current.screenWidthDp * 0.4) * (1f - scrollBehavior.scrollFraction)).dp)
//              .padding(end = 16.dp)
//              .clip(RoundedCornerShape(16.dp))
//              .alpha(1f - scrollBehavior.scrollFraction)
//          )
//        }
//      }

//      Column(
//        modifier = Modifier
//          .fillMaxHeight()
//          .background(
//            brush = Brush.verticalGradient(
//              colors = listOf(
//                dominantColorAsBg.value.copy(0.3f),
//                MaterialTheme.colorScheme.background
//              )
//            )
//          )
//          .padding(top = 16.dp)
//          .statusBarsPadding()
//      ) {
//
//
//        MediumText(text = item.text!!.title!!, fontSize = 21.sp, modifier = Modifier
//          .padding(horizontal = 16.dp)
//          .padding(top = 8.dp))
//
//        if (item.metadata!!.album!!.artists.size == 1) {
//          // large
//          Row(modifier = Modifier
//            .clickable(
//              interactionSource = remember { MutableInteractionSource() },
//              indication = null
//            ) {
//              HubEventHandler.handle(
//                navController,
//                delegate,
//                HubEvent.NavigateToUri(NavigateUri(item.metadata.album!!.artists[0].uri!!))
//              )
//            }
//            .padding(horizontal = 16.dp)
//            .padding(vertical = 12.dp)) {
//            AsyncImage(model = item.metadata.album!!.artists.first().images!![0].uri, contentScale = ContentScale.Crop, contentDescription = null, modifier = Modifier
//              .clip(CircleShape)
//              .size(32.dp))
//            MediumText(text = item.metadata.album.artists.first().name!!, fontSize = 13.sp, modifier = Modifier
//              .align(Alignment.CenterVertically)
//              .padding(start = 12.dp))
//          }
//        } else {
//          Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
//            item.metadata.album!!.artists.forEachIndexed { idx, artist ->
//              MediumText(text = artist.name!!, fontSize = 13.sp, modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
//                HubEventHandler.handle(
//                  navController,
//                  delegate,
//                  HubEvent.NavigateToUri(NavigateUri(artist.uri!!))
//                )
//              })
//
//              if (idx != item.metadata.album.artists.lastIndex) {
//                MediumText(text = " • ", fontSize = 13.sp)
//              }
//            }
//          }
//        }
//
//        Subtext(text = "${item.metadata.album!!.type} • ${item.metadata.album.year}", modifier = Modifier.padding(horizontal = 16.dp))
//
//        EntityActionStrip(navController, delegate, item)
//      }
//    },
//    navigationIcon = {
//      IconButton(onClick = { navController.popBackStack() }) {
//        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
//      }
//    },
//    scrollBehavior = scrollBehavior,
//    maxHeight = 300.dp,
//    contentPadding = PaddingValues(
//      top = with(LocalDensity.current) {
//        WindowInsets.statusBars.getTop(LocalDensity.current).toDp()
//      }
//    )
//  )
}