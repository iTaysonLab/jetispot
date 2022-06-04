package bruhcollective.itaysonlab.jetispot.ui.screens.auth

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.*
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpAuthManager
import bruhcollective.itaysonlab.jetispot.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.screens.Dialog
import bruhcollective.itaysonlab.jetispot.ui.screens.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthScreen(
  navController: LambdaNavigationController,
  viewModel: AuthScreenViewModel = hiltViewModel()
) {
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val autofill = LocalAutofill.current
  val focusManager = LocalFocusManager.current

  val usernameFocusRequester = remember { FocusRequester() }
  val passwordFocusRequester = remember { FocusRequester() }

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
      Autofill(
        autofillTypes = listOf(AutofillType.EmailAddress, AutofillType.Username),
        onFill = { viewModel.username.value = TextFieldValue(it) }
      ) { autofillNode ->
        OutlinedTextField(value = viewModel.username.value,
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
          ),
          keyboardActions = KeyboardActions(onNext = {
            passwordFocusRequester.requestFocus()
          }),
          singleLine = true,
          label = {
            Text(stringResource(R.string.username))
          }, onValueChange = { viewModel.username.value = it }, modifier = Modifier.fillMaxWidth().focusTarget().focusRequester(usernameFocusRequester).onFocusChanged {
            autofill?.apply {
              if (it.isFocused) {
                requestAutofillForNode(autofillNode)
              } else {
                cancelAutofillForNode(autofillNode)
              }
            }
          }.focusProperties { next = passwordFocusRequester },
        )
      }

      Spacer(Modifier.height(8.dp))

      Autofill(
        autofillTypes = listOf(AutofillType.Password),
        onFill = { viewModel.password.value = TextFieldValue(it) }
      ) { autofillNode ->
        OutlinedTextField(
          value = viewModel.password.value,
          label = {
            Text(stringResource(R.string.password))
          },
          singleLine = true,
          onValueChange = { viewModel.password.value = it },
          visualTransformation = if (viewModel.passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
          modifier = Modifier.fillMaxWidth().focusTarget().focusRequester(passwordFocusRequester).onFocusChanged {
            autofill?.apply {
              if (it.isFocused) {
                requestAutofillForNode(autofillNode)
              } else {
                cancelAutofillForNode(autofillNode)
              }
            }
          }.focusProperties { previous = usernameFocusRequester },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
          keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            scope.launch {
              viewModel.auth(snackbarHostState, navController)
            }
          }),
          trailingIcon = {
            val image = if (viewModel.passwordVisible.value)
              Icons.Rounded.Visibility
            else Icons.Rounded.VisibilityOff

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
    }

    Box(
      Modifier
        .align(Alignment.BottomStart)
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .offset(y = (80).dp)
    ) {
      OutlinedButton(onClick = {
        navController.navigate(Dialog.AuthDisclaimer)
      }, modifier = Modifier.align(Alignment.CenterStart)) {
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

@ExperimentalComposeUiApi
@Composable
private fun Autofill(
  autofillTypes: List<AutofillType>,
  onFill: ((String) -> Unit),
  content: @Composable (AutofillNode) -> Unit
) {
  val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes)

  val autofillTree = LocalAutofillTree.current
  autofillTree += autofillNode

  Box(
    Modifier.onGloballyPositioned {
      autofillNode.boundingBox = it.boundsInWindow()
    }
  ) {
    content(autofillNode)
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

  suspend fun auth(shs: SnackbarHostState, nc: LambdaNavigationController) {
    if (isAuthInProcess.value) return
    isAuthInProcess.value = true

    if (username.value.text.isEmpty() || password.value.text.isEmpty()) {
      shs.showSnackbar(nc.string(R.string.auth_err_empty))
      isAuthInProcess.value = false
      return
    }

    when (val result = authManager.authWith(username.value.text, password.value.text)) {
      is SpAuthManager.AuthResult.Exception -> {
        shs.showSnackbar("Java Error: ${result.e.message}")
      }
      is SpAuthManager.AuthResult.SpError -> {
        shs.showSnackbar(
          when (result.msg) {
            "BadCredentials" -> nc.string(R.string.auth_err_badcreds)
            "PremiumAccountRequired" -> nc.string(R.string.auth_err_premium)
            else -> "Spotify API error: ${result.msg}"
          }
        )
      }
      SpAuthManager.AuthResult.Success -> {
        nc.navigateAndClearStack(Screen.Feed)
      }
    }

    isAuthInProcess.value = false
  }
}