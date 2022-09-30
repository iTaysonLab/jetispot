package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.ext.copy
import bruhcollective.itaysonlab.jetispot.R

@Composable
fun PagingErrorPage(
  onReload: () -> Unit,
  exception: Exception,
  modifier: Modifier
) {
  val ctx = LocalContext.current

  Box(modifier) {
    Column(
      Modifier
        .align(Alignment.Center)
    ) {
      Icon(
        Icons.Rounded.Error, contentDescription = null, modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .size(56.dp)
          .padding(bottom = 12.dp)
      )
      Text(
        stringResource(id = R.string.err_text),
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
    }

    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 16.dp)
    ) {
      OutlinedButton(
        onClick = {
          ctx.copy("Message: ${exception.message}\n\n" + exception.stackTraceToString())
        }) {
        Text(stringResource(id = R.string.err_act_copy))
      }
      
      Spacer(modifier = Modifier.width(8.dp))

      OutlinedButton(
        onClick = { onReload() }) {
        Text(stringResource(id = R.string.err_act_reload))
      }
    }
  }
}

@Composable
fun PagingInfoPage(
  title: String,
  text: String,
  modifier: Modifier
) {
  Box(modifier) {
    Column(
      Modifier
        .align(Alignment.Center)
    ) {
      Icon(
        Icons.Rounded.Info, contentDescription = null, modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .size(56.dp)
      )

      Text(
        title,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )

      Text(
        text,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
    }
  }
}