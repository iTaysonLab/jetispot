package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.hub.LocalHubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.shared.MarqueeText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.ImageBackgroundTopAppBar
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.ImageTopAppBar
import coil.compose.AsyncImage

@OptIn(ExperimentalTextApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlaylistHeader(
  item: HubItem,
  scrollBehaviour: TopAppBarScrollBehavior
) {
  val navController = LocalNavigationController.current
  val delegate = LocalHubScreenDelegate.current
//  val darkTheme = isSystemInDarkTheme()
//  val dominantColor = remember { mutableStateOf(Color.Transparent) }
//  val dominantColorAsBg = animateColorAsState(dominantColor.value)

//  LaunchedEffect(Unit) {
//    launch {
//      if (dominantColor.value != Color.Transparent) return@launch
//      dominantColor.value =
//        delegate.calculateDominantColor(item.images?.main?.uri.toString(), darkTheme)
//    }
//  }

  ImageTopAppBar(
    title = {
      MarqueeText(
        text = item.text?.title!!,
        overflow = TextOverflow.Ellipsis,
      )
    },
    smallTitle = {
      Text(
        text = item.text?.title!!,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    },
    artwork = {
      PreviewableAsyncImage(
        item.images?.main?.uri,
        "playlist",
        modifier = Modifier.fillMaxSize()
      )
    },
    description = {
      item.text?.subtitle?.let {
        Column(Modifier.height(96.dp), verticalArrangement = Arrangement.Bottom) {
          Row(
            Modifier
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
              ) { navController.navigate(item.custom?.get("owner_username") as String) }
          ) {
            PreviewableAsyncImage(
              imageUrl = item.custom?.get("owner_pic") as String,
              placeholderType = "user",
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
            )

            Text(
              text = item.custom["owner_name"] as String,
              fontSize = 14.sp,
              modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 12.dp)
            )
          }
//          Text(
//            text = "${item.custom?.get("likes_count") as Long} likes • ${item.custom["total_duration"] as String}",
//            fontSize = 12.sp,
//            maxLines = 1
//          )

          Text(
            text = it,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            modifier = Modifier.padding(top = if (it != "") 8.dp else 0.dp)
          )
        }
      }
    },
    actions = {
      Icon(Icons.Rounded.Favorite, contentDescription = null)
      IconButton(onClick = { /*TODO*/ }) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Options for ${item.text!!.title!!} by ${item.text!!.subtitle!!}"
        )
      }
    },
    scrollBehavior = scrollBehaviour,
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
//    PlaylistHeaderAdditionalInfo(navController, delegate, item.custom)
//    EntityActionStrip(navController, delegate, item)
//  }
}


@OptIn(ExperimentalTextApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LargePlaylistHeader(
  item: HubItem,
  scrollBehavior: TopAppBarScrollBehavior
) {
  val navController = LocalNavigationController.current

  ImageBackgroundTopAppBar(
    description = {
      Text(
        item.text!!.subtitle!!,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
        modifier = Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
        maxLines = 8,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
      )
    },
    title = {
      MarqueeText(
        item.text!!.title!!,
        Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis
      )
    },
    smallTitle = {
      MarqueeText(
        item.text!!.title!!,
        Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis
      )
    },
    picture = {
      AsyncImage(
        model = item.images?.main?.uri,
        contentDescription = null,
        Modifier
          .fillMaxSize(),
        contentScale = ContentScale.FillWidth
      )
    },
    navigationIcon = {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
      }
    },
    actions = {
      IconButton(
        onClick = { /*TODO*/ },
        modifier = Modifier
          .clip(CircleShape)
          .size(38.dp)
          .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp).copy(0.5f))
      ) {
        Icon(Icons.Rounded.Favorite, contentDescription = null)
      }

      Spacer(modifier = Modifier.padding(horizontal = 4.dp))

      IconButton(
        onClick = { /*TODO*/ },
        modifier = Modifier
          .clip(CircleShape)
          .size(38.dp)
          .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp).copy(0.5f))
      ) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Options for ${item.text!!.title!!} by ${item.text!!.subtitle!!}",
        )
      }
    },
    scrollBehavior = scrollBehavior,
    maxHeight = 256.dp,
    gradient = true,
    navigationIconPresent = true
  )

//  Column {
//    Box(
//      Modifier
//        .fillMaxWidth()
//        .height(240.dp)
//    ) {
//    PlaylistHeaderAdditionalInfo(navController, delegate, item.custom)
//    EntityActionStrip(navController, delegate, item)
//  }
}


@Composable
fun PlaylistHeaderAdditionalInfo(
  custom: Map<String, Any>?
) {
  custom ?: return
  Row(
    Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
  ) {


  }

  Spacer(modifier = Modifier.height(6.dp))
}