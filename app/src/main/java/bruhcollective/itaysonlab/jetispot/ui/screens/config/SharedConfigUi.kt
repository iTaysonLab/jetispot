package bruhcollective.itaysonlab.jetispot.ui.screens.config

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpConfigurationManager
import bruhcollective.itaysonlab.jetispot.proto.AppConfig
import bruhcollective.itaysonlab.jetispot.ui.ext.rememberEUCScrollBehavior
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import bruhcollective.itaysonlab.jetispot.ui.navigation.NavigationController
import kotlinx.coroutines.launch

interface ConfigViewModel {
  suspend fun modifyDatastore (runOnBuilder: AppConfig.Builder.() -> Unit)
  fun provideDataStore(): DataStore<AppConfig>
  fun provideConfigList(): List<ConfigItem>
  @StringRes fun provideTitle(): Int
  fun isRoot(): Boolean = false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseConfigScreen(
  viewModel: ConfigViewModel
) {
  val scrollBehavior = rememberEUCScrollBehavior()

  val scope = rememberCoroutineScope()
  val dsConfigState = viewModel.provideDataStore().data.collectAsState(initial = SpConfigurationManager.DEFAULT)
  val dsConfig = dsConfigState.value
  val navController = LocalNavigationController.current

  Scaffold(topBar = {
    LargeTopAppBar(title = {
      Text(stringResource(viewModel.provideTitle()))
    }, navigationIcon = {
      if (!viewModel.isRoot()) {
        IconButton(onClick = { navController.popBackStack() }) {
          Icon(Icons.Rounded.ArrowBack, null)
        }
      }
    }, scrollBehavior = scrollBehavior)
  }, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) { padding ->
    LazyColumn(
      Modifier
        .fillMaxHeight()
        .padding(padding)) {
      items(viewModel.provideConfigList()) { item ->
        when (item) {
          is ConfigItem.Category -> {
            ConfigCategory(stringResource(item.title))
          }

          is ConfigItem.Info -> {
            ConfigInfo(stringResource(item.text))
          }

          is ConfigItem.BlendInfo -> {
            BlendInfo(stringResource(item.text))
          }

          is ConfigItem.Preference -> {
            ConfigPreference(
              stringResource(item.title),
              item.subtitle(LocalContext.current, dsConfig)
            ) {
              item.onClick(navController)
            }
          }

          is ConfigItem.BlendButton -> {
            BlendButton(
              stringResource(item.title),
              item.subtitle(LocalContext.current, dsConfig)
            ) {
              item.onClick(navController)
            }
          }

          is ConfigItem.Switch -> {
            ConfigSwitch(stringResource(item.title), stringResource(item.subtitle), item.switchState(dsConfig)) { newValue ->
              scope.launch { viewModel.modifyDatastore { item.modify(this, newValue) }}
            }
          }

          is ConfigItem.LargeSwitch -> {
            ConfigLargeSwitch(stringResource(item.title), item.switchState(dsConfig)) { newValue ->
              scope.launch { viewModel.modifyDatastore { item.modify(this, newValue) }}
            }
          }

          is ConfigItem.Radio -> {
            ConfigRadio(stringResource(item.title), stringResource(item.subtitle), item.radioState(dsConfig), item.enabledState(dsConfig)) {
              scope.launch { viewModel.modifyDatastore { item.modify(this) }}
            }
          }

          is ConfigItem.Slider -> {
            ConfigSlider(stringResource(item.title), item.subtitle, item.range, item.stepCount, item.state(dsConfig)) { newValue ->
              scope.launch { viewModel.modifyDatastore { item.modify(this, newValue) }}
            }
          }

          is ConfigItem.BlendPreview -> {BlendPreview()}
        }
      }
    }
  }
}

@Composable
@Stable
fun ConfigCategory(
  text: String
) {
  Text(
    text = text,
    color = MaterialTheme.colorScheme.primary,
    fontSize = 14.sp,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
  )
}

@Composable
@Stable
fun ConfigInfo(
  text: String
) {
  Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
    Icon(Icons.Rounded.Info, contentDescription = null, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)

    Text(
      text = text,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 14.sp,
      modifier = Modifier.padding(top = 12.dp)
    )
  }
}

@Composable
fun BlendInfo(
  text: String
) {
  Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
    Text(
      text = text,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 14.sp,
      modifier = Modifier.padding(top = 12.dp),
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun ConfigSwitch(
  title: String,
  subtitle: String,
  value: Boolean,
  onClick: (Boolean) -> Unit
) {
  Row(modifier = Modifier
    .fillMaxWidth()
    .clickable {
      onClick(!value)
    }
    .padding(16.dp)) {

    Column(
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .align(Alignment.CenterVertically)
    ) {
      Text(text = title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp)
      if (subtitle.isNotEmpty()) Text(
        text = subtitle,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 4.dp)
      )
    }

    Switch(
      checked = value, onCheckedChange = {}, modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.CenterVertically)
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigLargeSwitch(
  title: String,
  value: Boolean,
  onClick: (Boolean) -> Unit
) {
  val color = animateColorAsState(targetValue = if (value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.5f).compositeOver(MaterialTheme.colorScheme.inverseSurface))
  Card(colors = CardDefaults.cardColors(containerColor = color.value), shape = RoundedCornerShape(28.dp), onClick = {
     onClick(!value)
  }, modifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 16.dp)
    .padding(bottom = 8.dp)) {
    Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
      Text(text = title, color = MaterialTheme.colorScheme.inverseOnSurface, fontSize = 20.sp, modifier = Modifier
        .fillMaxWidth(0.85f)
        .align(Alignment.CenterVertically))

      Switch(
        colors = SwitchDefaults.colors(
          checkedTrackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
          checkedThumbColor = MaterialTheme.colorScheme.primary,
        ),
        checked = value, onCheckedChange = {}, modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.CenterVertically)
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigRadio(
  title: String,
  subtitle: String,
  value: Boolean,
  enabled: Boolean = true,
  onClick: () -> Unit
) {
  Row(modifier = Modifier
    .fillMaxWidth()
    .clickable(enabled) { onClick() }
    .padding(vertical = 16.dp, horizontal = 6.dp)) {

    RadioButton(selected = value, onClick = { onClick() }, enabled = enabled, modifier = Modifier.align(Alignment.CenterVertically))

    Column(
      modifier = Modifier
        .padding(start = 16.dp)
        .fillMaxWidth()
        .align(Alignment.CenterVertically)
        .alpha(if (enabled) 1f else 0.7f)
    ) {
      Text(text = title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp)
      if (subtitle.isNotEmpty()) Text(
        text = subtitle,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 4.dp)
      )
    }
  }
}

@Composable
fun ConfigPreference(
  title: String,
  subtitle: String = "",
  onClick: () -> Unit
) {
  Column(modifier = Modifier
    .fillMaxWidth()
    .clickable { onClick() }
    .padding(16.dp)) {
    Text(text = title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp)
    if (subtitle.isNotEmpty()) Text(
      text = subtitle,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 14.sp,
      modifier = Modifier.padding(top = 4.dp)
    )
  }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun BlendButton(
  title: String,
  subtitle: String = "",
  onClick: () -> Unit
) {
  /*
  Box(
    Modifier
      .padding(16.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
  ) {
    Column(Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp)
      if (subtitle.isNotEmpty()) Text(
        text = subtitle,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 4.dp)
      )
    }
  }
  */
  Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Box(
      Modifier
        .padding(16.dp)
        .height(40.dp)
        .clip(RoundedCornerShape(64.dp))
        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
        .clickable{onClick()},
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(horizontal = 24.dp))
    }
  }
}

@Composable
fun ConfigSlider(
  title: String,
  subtitleFunc: (Context, Int) -> String,
  range: ClosedFloatingPointRange<Float>,
  stepCount: Int,
  initialValue: Int,
  onValueChange: (Int) -> Unit
) {
  val sliderValueFirst = remember { initialValue.toFloat() }
  val sliderValueWAApplied = remember { mutableStateOf(false) }
  var sliderValue by remember { mutableStateOf(sliderValueFirst) }

  // a slight workaround for datastore's collectAsState initial
  if (!sliderValueWAApplied.value && initialValue.toFloat() != sliderValueFirst) {
    sliderValueWAApplied.value = true
    sliderValue = initialValue.toFloat()
  }

  val subtitle = subtitleFunc(LocalContext.current, sliderValue.toInt())

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp, bottom = 6.dp)
  ) {
    Box(Modifier.fillMaxWidth()) {
      Text(text = title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterStart))
      if (subtitle.isNotEmpty()) Text(
        text = subtitle,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        modifier = Modifier.align(Alignment.CenterEnd)
      )
    }

    Slider(
      value = sliderValue,
      onValueChange = { sliderValue = it },
      onValueChangeFinished = { onValueChange(sliderValue.toInt()) },
      modifier = Modifier.padding(top = 4.dp),
      valueRange = range,
      steps = stepCount
    )
  }
}

@Composable
fun BlendPreview() {
  Box(modifier = Modifier
    .padding(16.dp)
    .fillMaxWidth(), contentAlignment = Alignment.Center){
    Row() {
      SpotifyBox(
        backgroundColor = Color(0xFF1A2C2C),
        circleColor = Color(0xFF016450),
        centerColor = Color(0xFF008644),
        bigBox = false
      )
      Spacer(modifier = Modifier.size(50.dp))
      SpotifyBox(
        backgroundColor = Color(0xFF231419),
        circleColor = Color(0xFF8D1832),
        centerColor = Color(0xFFB91641),
        bigBox = false
      )
    }
    SpotifyBox(
      backgroundColor = Color(0xFF2C231A),
      circleColor = Color(0xFFF59B23),
      centerColor = Color(0xFFFABE10),
      bigBox = true
    )
  }
}

@Composable
private fun SpotifyBox(backgroundColor: Color, circleColor: Color, centerColor: Color, bigBox: Boolean){
  Box(
    modifier = Modifier
      .size(if (bigBox) 175.dp else 125.dp)
      .background(backgroundColor)
  ){
    androidx.compose.material.Icon(
      ImageVector.vectorResource(id = R.drawable.spotify_logo),
      contentDescription = null,
      modifier = Modifier
        .padding(4.dp)
        .align(Alignment.TopStart)
        .size(if (bigBox) 16.dp else 12.dp),
      tint = Color.White
    )
    Box(
      modifier = Modifier
        .size(if (bigBox) 90.dp else 70.dp)
        .align(Alignment.Center)
    ){
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .background(circleColor)
          .size(if (bigBox) 65.dp else 45.dp)
          .align(Alignment.BottomStart)
      )

      Box(
        modifier = Modifier
          .clip(CircleShape)
          .background(centerColor)
          .size(if (bigBox) 65.dp else 45.dp)
          .align(Alignment.TopEnd)
      )
    }
    Box(
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .align(Alignment.BottomStart)
    ){
      androidx.compose.material.Text(
        "Blend",
        fontWeight = FontWeight(1000),
        fontSize = if (bigBox) 16.sp else 12.sp,
        color = Color.White,
        modifier = Modifier.padding(vertical = if (bigBox) 14.dp else 8.dp)
      )
      Box(modifier = Modifier
        .size(width = if (bigBox) 44.dp else 32.dp, height = if (bigBox) 6.dp else 4.dp)
        .background(circleColor)
        .align(Alignment.BottomEnd))
    }
  }
}

//

sealed class ConfigItem {
  class Category(@StringRes val title: Int) : ConfigItem()
  class Info(@StringRes val text: Int) : ConfigItem()
  class BlendInfo(@StringRes val text: Int) : ConfigItem()

  class Preference(
    @StringRes val title: Int,
    val subtitle: (Context, AppConfig) -> String,
    val onClick: (NavigationController) -> Unit
  ) : ConfigItem()

  class BlendButton(
    @StringRes val title: Int,
    val subtitle: (Context, AppConfig) -> String,
    val onClick: (NavigationController) -> Unit
  ) : ConfigItem()

  class Switch(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val switchState: (AppConfig) -> Boolean,
    val modify: AppConfig.Builder.(value: Boolean) -> Unit
  ) : ConfigItem()

  class LargeSwitch(
    @StringRes val title: Int,
    val switchState: (AppConfig) -> Boolean,
    val modify: AppConfig.Builder.(value: Boolean) -> Unit
  ) : ConfigItem()

  class Radio(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    val radioState: (AppConfig) -> Boolean,
    val enabledState: (AppConfig) -> Boolean,
    val modify: AppConfig.Builder.() -> Unit
  ) : ConfigItem()

  class Slider(
    @StringRes val title: Int,
    val subtitle: (Context, Int) -> String,
    val range: ClosedFloatingPointRange<Float>,
    val stepCount: Int,
    val state: (AppConfig) -> Int,
    val modify: AppConfig.Builder.(Int) -> Unit
  ) : ConfigItem()
  
  class BlendPreview() : ConfigItem()
}