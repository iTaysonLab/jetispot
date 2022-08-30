package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.hub.components.essentials.EntityActionStrip
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.MarqueeText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.ImageBackgroundTopAppBar
import dev.chrisbanes.snapper.ExperimentalSnapperApi


@OptIn(ExperimentalSnapperApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlbumHeader(
  item: HubItem,
  scrollBehavior: TopAppBarScrollBehavior
) {
  val navController = LocalNavigationController.current
  val delegate = LocalHubScreenDelegate.current
  val artistScrollState = rememberLazyListState()

  Column() {
    ImageBackgroundTopAppBar(
      maxHeight = 224.dp,
      title = {
//        Text(
//          item.text!!.title!!,
//          overflow = TextOverflow.Ellipsis,
//          maxLines = 3
//        )
      },
      smallTitle = {
        MarqueeText(
          item.text!!.title!!,
          overflow = TextOverflow.Ellipsis
        )
      },
      picture = {
        PreviewableAsyncImage(
          item.images?.main?.uri, item.images?.main?.placeholder,
          modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
        )
      },
      description = {
//        Column() {
//          LazyRow(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(64.dp),
//            state = artistScrollState,
//            flingBehavior = rememberSnapperFlingBehavior(
//              artistScrollState,
//              snapOffsetForItem = SnapOffsets.Start,
//              springAnimationSpec = spring(dampingRatio = 0.001f, stiffness = 10f)
//            )
//          ) {
//            items(item.metadata?.album!!.artists) {
//              Row(
//                modifier = Modifier
//                  .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null
//                  ) {
//                    HubEventHandler.handle(
//                      navController,
//                      delegate,
//                      HubEvent.NavigateToUri(NavigateUri(it.uri!!))
//                    )
//                  }
//              ) {
//
//                AsyncImage(
//                  model = it.images!![0].uri,
//                  contentScale = ContentScale.Crop,
//                  contentDescription = null,
//                  modifier = Modifier
//                    .size(32.dp)
//                    .clip(CircleShape)
//                )
//
//                Text(
//                  text = it.name!!,
//                  fontSize = 14.sp,
//                  modifier = Modifier
//                    .align(Alignment.CenterVertically)
//                    .padding(start = 12.dp, end = 64.dp)
//                )
//              }
//            }
//          }
//
//          Subtext(
//            text = "${item.metadata?.album!!.type} • ${item.metadata.album.year}",
//            modifier = Modifier.padding(top = 8.dp)
//          )
//        }
      },
      navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
          Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
        }
      },
      actions = {
//        IconButton(
//          onClick = { /*TODO*/ },
//          modifier = Modifier
//            .clip(CircleShape)
//            .size(38.dp)
//            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp).copy(0.5f))
//        ) {
//          Icon(Icons.Rounded.Favorite, contentDescription = null)
//        }
//
//        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        IconButton(
          onClick = { /*TODO*/ },
          modifier = Modifier
            .clip(CircleShape)
            .size(38.dp)
            .background(
              MaterialTheme.colorScheme
                .surfaceColorAtElevation(4.dp)
                .copy(0.5f)
            )
        ) {
          Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Options for ${item.text!!.title!!} by ${item.text!!.subtitle!!}"
          )
        }
      },
      scrollBehavior = scrollBehavior,
      gradient = false,
      navigationIconPresent = true,
    )

    Box(
      Modifier.height((88 * (1f - scrollBehavior.state.collapsedFraction)).dp),
      contentAlignment = Alignment.Center
    ) {
      EntityActionStrip(delegate, item, scrollBehavior)
    }
  }

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
//      }
//    },
//  )
}