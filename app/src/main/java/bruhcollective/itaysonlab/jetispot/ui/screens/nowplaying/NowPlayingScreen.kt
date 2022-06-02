package bruhcollective.itaysonlab.jetispot.ui.screens.nowplaying

import android.text.format.DateUtils
import androidx.annotation.StringRes
import androidx.collection.LruCache
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpPlayerServiceManager
import bruhcollective.itaysonlab.jetispot.core.api.SpPartnersApi
import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import bruhcollective.itaysonlab.jetispot.ui.shared.*
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.spotify.metadata.Metadata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
fun NowPlayingScreen(
  navController: NavController,
  bottomSheetState: BottomSheetState,
  bsOffset: Float,
  viewModel: NowPlayingViewModel = hiltViewModel()
) {
  val mainPagerState = rememberPagerState()
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    // one-time VM-UI connection
    viewModel.uiOnTrackIndexChanged = { new ->
      scope.launch { mainPagerState.animateScrollToPage(new) }
    }
  }

  Box(Modifier.fillMaxSize()) {
    Box(modifier = Modifier.fillMaxSize()) {
      NowPlayingBackground(
        state = mainPagerState,
        viewModel = viewModel,
        modifier = Modifier.fillMaxSize(),
      )

      // main content
      NowPlayingHeader(
        stateTitle = viewModel.getHeaderTitle(),
        onCloseClick = {
          scope.launch { bottomSheetState.collapse() }
        },
        state = viewModel.getHeaderText(),
        modifier = Modifier
          .statusBarsPadding()
          .align(Alignment.TopCenter)
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )

      NowPlayingControls(
        viewModel = viewModel, modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(horizontal = 8.dp)
          .padding(bottom = 24.dp)
          .navigationBarsPadding()
      )
    }

    NowPlayingMiniplayer(
      viewModel,
      Modifier
        .clickable { scope.launch { bottomSheetState.expand() } }
        .fillMaxWidth()
        .height(72.dp)
        .align(Alignment.TopStart)
        .alpha(1f - bsOffset)
    )
  }
}

@Composable
fun NowPlayingMiniplayer(
  viewModel: NowPlayingViewModel,
  modifier: Modifier
) {
  Surface(tonalElevation = 8.dp, modifier = modifier) {
    Box(Modifier.fillMaxSize()) {
      LinearProgressIndicator(
        progress = viewModel.currentPosition.value.progressRange,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .height(2.dp)
          .fillMaxWidth()
      )

      Row(
        Modifier
          .fillMaxHeight()
          .padding(horizontal = 16.dp)
      ) {
        PreviewableSyncImage(
          viewModel.currentTrack.value.artworkCompose,
          placeholderType = "track",
          modifier = Modifier
            .size(48.dp)
            .align(Alignment.CenterVertically)
            .clip(RoundedCornerShape(8.dp))
        )

        Column(
          Modifier
            .weight(2f)
            .padding(horizontal = 14.dp)
            .align(Alignment.CenterVertically)
        ) {
          Text(
            viewModel.currentTrack.value.title,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp
          )
          Text(
            viewModel.currentTrack.value.artist,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp)
          )
        }

        PlayPauseButton(
          viewModel.currentState.value == SpPlayerServiceManager.PlaybackState.Playing,
          { viewModel.togglePlayPause() },
          MaterialTheme.colorScheme.onSurface,
          Modifier
            .fillMaxHeight()
            .width(56.dp)
            .align(Alignment.CenterVertically)
        )
      }
    }
  }
}

@Composable
fun NowPlayingHeader(
  @StringRes stateTitle: Int,
  state: String,
  onCloseClick: () -> Unit,
  modifier: Modifier
) {
  Row(modifier, verticalAlignment = Alignment.CenterVertically) {
    IconButton(onClick = onCloseClick, Modifier.size(32.dp)) {
      Icon(imageVector = Icons.Default.ArrowDownward, tint = Color.White, contentDescription = null)
    }

    Column(Modifier.weight(1f)) {
      Text(
        text = stringResource(id = stateTitle).uppercase(),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        textAlign = TextAlign.Center,
        color = Color.White.copy(alpha = 0.7f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        letterSpacing = 2.sp,
        fontSize = 12.sp
      )

      Text(
        text = state,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
      )
    }

    IconButton(onClick = { /*TODO*/ }, Modifier.size(32.dp)) {
      Icon(imageVector = Icons.Default.MoreVert, tint = Color.White, contentDescription = null)
    }
  }
}

@Composable
fun NowPlayingControls(
  viewModel: NowPlayingViewModel,
  modifier: Modifier
) {
  Column(modifier, verticalArrangement = Arrangement.Bottom) {
    // Header
    MediumText(text = viewModel.currentTrack.value.title, modifier = Modifier.padding(horizontal = 14.dp), fontSize = 24.sp, color = Color.White,)
    Spacer(Modifier.height(2.dp))
    Text(text = viewModel.currentTrack.value.artist, modifier = Modifier.padding(horizontal = 14.dp), maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f))
    Spacer(Modifier.height(8.dp))

    // Progressbar
    Slider(value = viewModel.currentPosition.value.progressRange, colors = SliderDefaults.colors(
      thumbColor = Color.White,
      activeTrackColor = Color.White,
      inactiveTrackColor = Color.White.copy(alpha = 0.5f)
    ), onValueChange = {}, modifier = Modifier.padding(horizontal = 8.dp))

    Row(Modifier.padding(horizontal = 14.dp).offset(y = (-4).dp)) {
      Text(text = DateUtils.formatElapsedTime(viewModel.currentPosition.value.progressMilliseconds / 1000L), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
      Spacer(modifier = Modifier.weight(1f))
      Text(text = DateUtils.formatElapsedTime(viewModel.currentTrack.value.duration / 1000L), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }

    Spacer(Modifier.height(16.dp))

    // Control Buttons
    Row {
      IconButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
      ) {
        Icon(imageVector = Icons.Default.Shuffle, contentDescription = null)
      }

      Spacer(modifier = Modifier.weight(1f))

      IconButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
      ) {
        Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = null)
      }

      Spacer(modifier = Modifier.width(24.dp))

      Surface(color = Color.White, modifier = Modifier.clip(CircleShape)) {
        PlayPauseButton(
          viewModel.currentState.value == SpPlayerServiceManager.PlaybackState.Playing,
          { viewModel.togglePlayPause() },
          Color.Black,
          Modifier
            .size(56.dp)
            .align(Alignment.CenterVertically)
        )
      }

      Spacer(modifier = Modifier.width(24.dp))

      IconButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
      ) {
        Icon(imageVector = Icons.Default.SkipNext, contentDescription = null)
      }

      Spacer(modifier = Modifier.weight(1f))

      IconButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
      ) {
        Icon(imageVector = Icons.Default.Repeat, contentDescription = null)
      }
    }

    Spacer(Modifier.height(16.dp))

    // Additional Buttons

    Row {
      IconButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
      ) {
        Icon(imageVector = Icons.Default.Share, contentDescription = null)
      }

      Spacer(modifier = Modifier.weight(1f))

      IconButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
      ) {
        Icon(imageVector = Icons.Default.QueueMusic, contentDescription = null)
      }
    }
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NowPlayingBackground(
  state: PagerState,
  viewModel: NowPlayingViewModel,
  modifier: Modifier
) {
  val dominantColorAsBg = animateColorAsState(viewModel.currentBgColor.value)
  Box(modifier = modifier.background(dominantColorAsBg.value)) {
    HorizontalPager(
      count = viewModel.currentQueue.value.size,
      state = state,
      modifier = modifier
    ) { page ->
      val artworkModifier = Modifier
        .align(Alignment.Center)
        .padding(bottom = (LocalConfiguration.current.screenHeightDp * 0.25).dp)
        .size((LocalConfiguration.current.screenWidthDp * 0.9).dp)

      if (page == viewModel.currentQueuePosition.value && viewModel.currentTrack.value.artworkCompose != null) {
        Image(viewModel.currentTrack.value.artworkCompose!!, contentDescription = null, modifier = artworkModifier, contentScale = ContentScale.Crop)
      } else {
        NowPlayingBackgroundItem(
          track = viewModel.currentQueue.value[page],
          modifier = artworkModifier
        )
      }
    }
  }
}

@Composable
fun NowPlayingBackgroundItem(
  track: Metadata.Track,
  modifier: Modifier,
) {
  Box(modifier) {
    ImagePreview("track", Modifier.fillMaxSize())
    AsyncImage(
      model = SpUtils.getImageUrl(track.album.coverGroup.imageList.find { it.size == Metadata.Image.Size.LARGE }?.fileId),
      contentDescription = null,
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop,
    )
  }
}

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
  private val spPlayerServiceManager: SpPlayerServiceManager,
  private val spPartnersApi: SpPartnersApi
) : ViewModel(), SpPlayerServiceManager.ServiceExtraListener, CoroutineScope by MainScope() {
  // states
  val currentTrack get() = spPlayerServiceManager.currentTrack
  val currentPosition get() = spPlayerServiceManager.playbackProgress
  val currentState get() = spPlayerServiceManager.playbackState
  val currentContext get() = spPlayerServiceManager.currentContext
  val currentContextUri get() = spPlayerServiceManager.currentContextUri
  val currentQueue get() = spPlayerServiceManager.currentQueue
  val currentQueuePosition get() = spPlayerServiceManager.currentQueuePosition
  val currentBgColor = mutableStateOf(Color.Transparent)

  // ui bridges
  var uiOnTrackIndexChanged: (Int) -> Unit = {}

  // caches
  private val imageCache = LruCache<String, Color>(10)
  private var imageColorTask: Job? = null

  fun togglePlayPause() {
    spPlayerServiceManager.playPause()
  }

  init {
    spPlayerServiceManager.registerExtra(this)
  }

  override fun onCleared() {
    spPlayerServiceManager.unregisterExtra(this)
  }

  override fun onTrackIndexChanged(new: Int) {
    if (currentQueue.value.isEmpty()) return
    uiOnTrackIndexChanged.invoke(new)

    imageColorTask?.cancel()
    imageColorTask = launch(Dispatchers.IO) {
      currentBgColor.value = calculateDominantColor(
        spPartnersApi,
        SpUtils.getImageUrl(currentQueue.value[new].album.coverGroup.imageList.find { it.size == Metadata.Image.Size.LARGE }?.fileId)
          ?: return@launch,
        false
      )
    }
  }

  fun getHeaderTitle(): Int {
    if (currentContextUri.value == "") return R.string.playing_src_unknown
    var uriSeparated = currentContextUri.value.split(":").drop(1)
    if (uriSeparated[0] == "user") uriSeparated = uriSeparated.drop(2)
    return when (uriSeparated[0]) {
      "collection" -> R.string.playing_src_library
      "playlist" -> R.string.playing_src_playlist
      "album" -> R.string.playing_src_album
      "artist" -> R.string.playing_src_artist
      else -> R.string.playing_src_unknown
    }
  }

  fun getHeaderText(): String {
    return when {
      currentContextUri.value.contains("collection") -> "Liked Songs" // TODO: to R.string
      else -> currentContext.value
    }
  }

  suspend fun calculateDominantColor(
    partnersApi: SpPartnersApi,
    url: String,
    dark: Boolean
  ): Color {
    return try {
      if (imageCache[url] != null) {
        return imageCache[url]!!
      }

      val apiResult =
        partnersApi.fetchExtractedColors(variables = "{\"uris\":[\"$url\"]}").data.extractedColors[0].let {
          if (dark) it.colorRaw else it.colorDark
        }.hex

      Color(android.graphics.Color.parseColor(apiResult)).also { imageCache.put(url, it) }
    } catch (e: Exception) {
      // e.printStackTrace()
      Color.Transparent
    }
  }
}