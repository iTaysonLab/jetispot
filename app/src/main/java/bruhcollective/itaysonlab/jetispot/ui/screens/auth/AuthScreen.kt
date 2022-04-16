package bruhcollective.itaysonlab.jetispot.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun AuthScreen(
  navController: NavController,
  viewModel: AuthScreenViewModel = hiltViewModel()
) {
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  Box(
    Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(
      Modifier
        .align(Alignment.TopCenter)
        .padding(top = 32.dp)
    ) {
      Text(
        text = stringResource(R.string.auth_welcome),
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.auth_welcome_text),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )
    }

    Column(
      Modifier
        .align(Alignment.Center)
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    ) {
      OutlinedTextField(value = viewModel.username.value,
        singleLine = true,
        label = {
          Text(stringResource(R.string.username))
        }, onValueChange = { viewModel.username.value = it }, modifier = Modifier.fillMaxWidth()
      )

      Spacer(Modifier.height(8.dp))

      OutlinedTextField(
        value = viewModel.password.value,
        label = {
          Text(stringResource(R.string.password))
        },
        singleLine = true,
        onValueChange = { viewModel.password.value = it },
        visualTransformation = if (viewModel.passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
          val image = if (viewModel.passwordVisible.value)
            Icons.Filled.Visibility
          else Icons.Filled.VisibilityOff

          // Please provide localized description for accessibility services
          val description =
            if (viewModel.passwordVisible.value) "Hide password" else "Show password"

          IconButton(onClick = {
            viewModel.passwordVisible.value = !viewModel.passwordVisible.value
          }) {
            Icon(imageVector = image, description)
          }
        }
      )
    }

    Box(
      Modifier
        .align(Alignment.BottomStart)
        .fillMaxWidth()
        .padding(vertical = 32.dp, horizontal = 16.dp)
    ) {
      OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.align(Alignment.CenterStart)) {
        Text(stringResource(R.string.auth_disclaimer))
      }

      Button(
        onClick = {
          scope.launch {
            viewModel.auth(snackbarHostState, navController)
          }
        },
        enabled = !viewModel.isAuthInProcess.value,
        modifier = Modifier.align(Alignment.CenterEnd)
      ) {
        Text(stringResource(R.string.auth_next))
      }
    }

    SnackbarHost(
      hostState = snackbarHostState,
      Modifier.align(Alignment.BottomStart),
      snackbar = { data ->
        Snackbar(
          containerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(12.dp),
          contentColor = MaterialTheme.colorScheme.onSurface,
          snackbarData = data
        )
      })
  }
}

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
  private val authManager: SpAuthManager
) : ViewModel() {
  val username = mutableStateOf(TextFieldValue())
  val password = mutableStateOf(TextFieldValue())

  val passwordVisible = mutableStateOf(false)
  val isAuthInProcess = mutableStateOf(false)

  suspend fun auth(shs: SnackbarHostState, nc: NavController) {
    if (isAuthInProcess.value) return
    isAuthInProcess.value = true
    when (val result = authManager.authWith(username.value.text, password.value.text)) {
      is SpAuthManager.AuthResult.Exception -> {
        shs.showSnackbar("Java Error: ${result.e.message}")
      }
      is SpAuthManager.AuthResult.SpError -> {
        shs.showSnackbar(
          when (result.msg) {
            "BadCredentials" -> nc.context.getString(R.string.auth_err_badcreds)
            "PremiumAccountRequired" ->  nc.context.getString(R.string.auth_err_premium)
            else -> "Spotify API error: ${result.msg}"
          }
        )
      }
      SpAuthManager.AuthResult.Success -> {
        nc.popBackStack()
        nc.navigate(Screen.Feed.route)
      }
    }
    isAuthInProcess.value = false
  }
}