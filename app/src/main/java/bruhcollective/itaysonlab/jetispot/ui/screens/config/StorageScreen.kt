package bruhcollective.itaysonlab.jetispot.ui.screens.config

import android.content.Context
import android.text.format.Formatter
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.metadata_db.SpMetadataDb
import bruhcollective.itaysonlab.jetispot.core.util.Device
import bruhcollective.itaysonlab.jetispot.ui.ext.blendWith
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.ext.findActivity
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import bruhcollective.itaysonlab.jetispot.ui.shared.PagingLoadingPage
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
  viewModel: StorageViewModel = hiltViewModel()
) {
  val scope = rememberCoroutineScope()
  val ctx = LocalContext.current

  val scrollBehavior = rememberEUCScrollBehavior()

  LaunchedEffect(Unit) {
    viewModel.load(ctx)
  }

  when (val state = viewModel.state.value) {
    StorageViewModel.UiState.Loading -> PagingLoadingPage(Modifier.fillMaxSize())
    is StorageViewModel.UiState.Ready -> {
      Scaffold(
        topBar = {
          LargeTopAppBar(title = { Text(stringResource(id = R.string.storage)) },
            navigationIcon = {
              IconButton(onClick = { ctx.findActivity().onBackPressed() }) {
                Icon(Icons.Rounded.ArrowBack, null)
              }
            },
            scrollBehavior = scrollBehavior
          )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) { padding ->
          LazyColumn(
            modifier = Modifier
              .fillMaxHeight()
              .padding(padding)
          ) {
          // 1. Large progress bar
          item("storagebar") {
            StorageHeader(state)
          }

          // 2. Sub-items
          items(viewModel.types) { type ->
            StorageComponentDetail(type, type.takenSize(state))
          }

          // 2-3. Header
          item("storageact") {
            ConfigCategory(text = stringResource(id = R.string.storage_actions))
          }

          // 3. Actions
          items(viewModel.clearActions) { type ->
            ConfigPreference(title = stringResource(id = type.title)) {
              viewModel.clearData(scope, ctx, type)
            }
          }
        }
      }
    }
  }
}

@Composable
fun StorageComponentDetail(
  type: StorageViewModel.StorageFileKind,
  size: String,
) {
  Row(
    Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp)) {
    Icon(imageVector = type.icon, contentDescription = null, modifier = Modifier.size(28.dp))

    Column(Modifier.padding(start = 16.dp)) {
      Text(text = stringResource(id = type.type), fontSize = 18.sp, fontWeight = FontWeight.Bold)
      Text(text = stringResource(id = type.desc), modifier = Modifier.padding(top = 2.dp), fontSize = 16.sp)
      Text(text = size, modifier = Modifier.padding(top = 2.dp), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
  }
}

@Composable
fun StorageHeader(
  state: StorageViewModel.UiState.Ready
) {
  Column(Modifier.padding(horizontal = 16.dp)) {
    Row {
      Text(
        text = state.fmtTotal,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        modifier = Modifier.offset(y = 8.dp)
      )

      Text(
        text = stringResource(id = R.string.storage_used),
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
          .align(Alignment.Bottom)
          .padding(start = 4.dp)
      )

      Spacer(Modifier.weight(1f))

      Text(
        text = stringResource(id = R.string.storage_total_fmt, state.fmtStorageTotal),
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.align(Alignment.Bottom)
      )
    }

    MultiStateProgressIndicator(
      Modifier
        .padding(top = 18.dp)
        .padding(horizontal = 8.dp)
        .fillMaxWidth()
        .height(16.dp), state.takenTotal, state.others, state.internalStorage.total
    )

    Row(Modifier.padding(top = 4.dp, bottom = 8.dp)) {
      ProgressIndicatorLegend(
        modifier = Modifier.align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.primary,
        text = stringResource(id = R.string.storage_legend_app)
      )

      ProgressIndicatorLegend(
        modifier = Modifier
          .padding(start = 16.dp)
          .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.compositeSurfaceElevation(8.dp)
          .blendWith(MaterialTheme.colorScheme.primary, 0.2f),
        text = stringResource(id = R.string.storage_legend_other)
      )
    }
  }
}

@Composable
fun ProgressIndicatorLegend(
  modifier: Modifier = Modifier,
  color: Color,
  text: String
) {
  Row(modifier) {
    Box(
      Modifier
        .clip(CircleShape)
        .size(18.dp)
        .background(color)
        .align(Alignment.CenterVertically)
    )

    Text(text,
      Modifier
        .padding(start = 8.dp)
        .align(Alignment.CenterVertically), fontSize = 12.sp)
  }
}

@Composable
fun MultiStateProgressIndicator(
  modifier: Modifier,
  application: Long,
  others: Long,
  total: Long,
) {
  val bgApp = MaterialTheme.colorScheme.primary
  val bgTotal = MaterialTheme.colorScheme.compositeSurfaceElevation(4.dp)
  val bgOthers = MaterialTheme.colorScheme.compositeSurfaceElevation(8.dp).blendWith(bgApp, 0.2f)

  Canvas(modifier) {
    val width = size.width
    val height = size.height

    // calc
    val othersPrcnt = others / total.toFloat()
    val appPrcnt = application / total.toFloat()
    val othersOffsetEnd = width * othersPrcnt
    val appOffsetEnd = othersOffsetEnd + (width * appPrcnt)

    // OTHERS - APPLICATION - REST
    drawLine(bgTotal, Offset(0f, 0f), Offset(width, 0f), height, StrokeCap.Round)
    drawLine(bgOthers, Offset(0f, 0f), Offset(othersOffsetEnd, 0f), height, StrokeCap.Round)
    drawLine(bgApp, Offset(othersOffsetEnd, 0f), Offset(appOffsetEnd, 0f), height, StrokeCap.Round)
  }
}

@HiltViewModel
class StorageViewModel @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val spMetadataDb: SpMetadataDb
) : ViewModel() {
  val types = StorageFileKind.values()
  val clearActions = ClearAction.values()

  private var clearJob: Job? = null

  var state = mutableStateOf<UiState>(UiState.Loading)
    private set

  @OptIn(ExperimentalCoilApi::class)
  suspend fun load(context: Context) = withContext(Dispatchers.Default) {
    val deviceSize = Device.getInternalStorageSize(context)
    val coilCache = context.imageLoader.diskCache

    val imgCache = File(context.cacheDir, "image_cache")
    val spaCache = File(context.cacheDir, "spa_cache")
    val metadata = File(File("${context.filesDir}", "spa_meta"), "metadata")

    state.value = UiState.Ready(
      context = context,
      internalStorage = deviceSize,
      takenMetadata = metadata.length(),
      takenTempCache = getFileSize(spaCache),
      takenImgCache = coilCache?.size ?: getFileSize(imgCache),
    )
  }

  private fun getFileSize(source: File): Long {
    return if (source.isDirectory) {
      source.listFiles()?.sumOf { getFileSize(it) } ?: 0L
    } else {
      source.length()
    }
  }

  @OptIn(ExperimentalCoilApi::class)
  fun clearData(scope: CoroutineScope, context: Context, type: ClearAction) {
    if (clearJob != null) return
    clearJob = scope.launch(Dispatchers.IO) {
      when (type) {
        ClearAction.ClearCaches -> {
          context.imageLoader.diskCache?.clear() ?: File(context.cacheDir, "image_cache").deleteRecursively()

          if (spSessionManager.isSignedIn()) {
            // use internal cache manager
            spSessionManager.session.cache().clearEverything()
          } else {
            // session is not created, we can safely delete the folder
            File(context.cacheDir, "spa_cache").deleteRecursively()
          }
        }

        ClearAction.ClearMetadata -> spMetadataDb.clear()
      }

      load(context)
      clearJob = null
    }
  }

  sealed class UiState {
    object Loading : UiState()

    class Ready(
      context: Context,
      val internalStorage: Device.StorageSize,
      val takenMetadata: Long,
      val takenTempCache: Long,
      val takenImgCache: Long
    ) : UiState() {
      val takenTotal = takenMetadata + takenTempCache + takenImgCache
      val others = internalStorage.taken - takenTotal

      val fmtTotal: String = takenTotal.formatSize(context)
      val fmtMeta: String = takenMetadata.formatSize(context)
      val fmtSpa: String = takenTempCache.formatSize(context)
      val fmtImg: String = takenImgCache.formatSize(context)
      val fmtStorageFree: String = internalStorage.free.formatSize(context)
      val fmtStorageTotal: String = internalStorage.total.formatSize(context)

      // UI
      private fun Long.formatSize(ctx: Context) = Formatter.formatFileSize(ctx, this)

      override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ready

        if (takenMetadata != other.takenMetadata) return false
        if (takenTempCache != other.takenTempCache) return false
        if (takenImgCache != other.takenImgCache) return false

        return true
      }

      override fun hashCode(): Int {
        var result = takenMetadata.hashCode()
        result = 31 * result + takenTempCache.hashCode()
        result = 31 * result + takenImgCache.hashCode()
        return result
      }
    }
  }

  enum class StorageFileKind(
    val icon: ImageVector,
    @StringRes val type: Int,
    @StringRes val desc: Int,
    val takenSize: (UiState.Ready) -> String,
  ) {
    SpaCache(Icons.Rounded.Cached, R.string.storage_entry_tmpcache, R.string.storage_entry_tmpcache_desc, { it.fmtSpa }),
    ImgCache(Icons.Rounded.Image, R.string.storage_entry_imgcache, R.string.storage_entry_imgcache_desc, { it.fmtImg }),
    Metadata(Icons.Rounded.Save, R.string.storage_entry_metadata, R.string.storage_entry_metadata_desc, { it.fmtMeta }),
  }

  enum class ClearAction(
    @StringRes val title: Int
  ) {
    ClearCaches(R.string.storage_clear),
    ClearMetadata(R.string.storage_clear_metadata)
  }
}
