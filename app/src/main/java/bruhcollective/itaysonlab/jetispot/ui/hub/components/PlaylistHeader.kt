package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.hub.HubScreenDelegate
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.ImageBackgroundTopAppBar
import bruhcollective.itaysonlab.jetispot.ui.shared.evo.ImageTopAppBar
import coil.compose.AsyncImage

@Composable
fun PlaylistHeader(
  navController: LambdaNavigationController,
  delegate: HubScreenDelegate,
  item: HubItem,
  scrollBehaviour: TopAppBarScrollBehavior
) {
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
      Text(
        text = item.text?.title!!,
        Modifier.fillMaxWidth(0.5f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 3
      )
    },
    smallTitle = {
      Text(
        text = item.text?.title!!,
        modifier = Modifier.fillMaxWidth(0.8f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )
    },
    artwork = {
      PreviewableAsyncImage(
        item.images?.main?.uri,
        "playlist",
        modifier = Modifier
          .fillMaxSize()
      )
    },
    artist = {},
    actions = {
      IconButton(onClick = { /*TODO*/ }) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Options for ${item.text!!.title!!} by ${item.text!!.subtitle!!}",
          tint = MaterialTheme.colorScheme.onBackground
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
//  Column(
//    Modifier
//      .fillMaxWidth()
//      .padding(top = 16.dp)
//      .statusBarsPadding()
//  ) {

//
//    MediumText(
//      text = item.text?.title!!, fontSize = 21.sp, modifier = Modifier
//        .padding(horizontal = 16.dp)
//        .padding(top = 8.dp)
//    )
//
//    if (!item.text.subtitle.isNullOrEmpty()) {
//      Text(
//        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
//        fontSize = 12.sp,
//        lineHeight = 18.sp,
//        text = item.text.subtitle, modifier = Modifier
//          .padding(horizontal = 16.dp)
//          .padding(top = 8.dp)
//      )
//    }
//
//    PlaylistHeaderAdditionalInfo(navController, delegate, item.custom)
//    EntityActionStrip(navController, delegate, item)
//  }
}


@OptIn(ExperimentalTextApi::class)
@Composable
fun LargePlaylistHeader(
  navController: LambdaNavigationController,
  delegate: HubScreenDelegate,
  item: HubItem,
  scrollBehavior: TopAppBarScrollBehavior
) {
  ImageBackgroundTopAppBar(
    aboveTitle = {
      Text(
        item.text!!.subtitle!!,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
        modifier = Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
        maxLines = 8,
//        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
      )
    },
    title = {
      Text(
        item.text!!.title!!,
        Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
        maxLines = 3
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
          .height(164.dp + WindowInsets.statusBars.getTop(LocalDensity.current).dp),
        contentScale = ContentScale.FillWidth
      )
    },
    navigationIcon = {
      IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
      }
    },
    contentPadding = PaddingValues(
      top = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(LocalDensity.current).toDp()
      }
    ),
    scrollBehavior = scrollBehavior,
    maxHeight = 256.dp,
    scrollHeight = -2.676f
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
  navController: LambdaNavigationController,
  delegate: HubScreenDelegate,
  custom: Map<String, Any>?
) {
  custom ?: return

  Spacer(modifier = Modifier.height(12.dp))

  Row(Modifier
    .clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = null
    ) { navController.navigate(custom["owner_username"] as String) }
    .fillMaxWidth()
    .padding(horizontal = 16.dp)) {
    PreviewableAsyncImage(
      imageUrl = custom["owner_pic"] as String, placeholderType = "user", modifier = Modifier
        .clip(CircleShape)
        .size(32.dp)
    )
    MediumText(
      text = custom["owner_name"] as String, fontSize = 13.sp, modifier = Modifier
        .align(Alignment.CenterVertically)
        .padding(start = 12.dp)
    )
  }

  Spacer(modifier = Modifier.height(12.dp))

  Row(
    Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
  ) {
    Icon(Icons.Rounded.Language, contentDescription = null, modifier = Modifier.size(26.dp))
    Text(
      text = "${custom["likes_count"] as Long} likes • ${custom["total_duration"] as String}",
      fontSize = 12.sp,
      maxLines = 1,
      modifier = Modifier
        .align(Alignment.CenterVertically)
        .padding(start = 8.dp)
    )
  }

  Spacer(modifier = Modifier.height(6.dp))
}