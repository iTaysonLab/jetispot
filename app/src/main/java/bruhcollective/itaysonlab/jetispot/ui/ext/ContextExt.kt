package bruhcollective.itaysonlab.jetispot.ui.ext

import android.app.Activity
import android.content.*
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.navigation.NavController

fun NavController.openUrl(url: String) = context.openUrl(url)
fun Context.openUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))

fun Context.shareUrl(url: String, title: String? = null) = startActivity(Intent(Intent.ACTION_SEND).apply {
  type = "text/plain"
  putExtra(Intent.EXTRA_TEXT, url)
  putExtra(Intent.EXTRA_TITLE, title)
}.let { Intent.createChooser(it, null) })

fun Context.copy(txt: String) {
  getSystemService<ClipboardManager>()?.setPrimaryClip(ClipData.newPlainText("jetispot", txt))
}

// Shamelessly taken from Google
internal fun Context.findActivity(): Activity {
  var context = this
  while (context is ContextWrapper) {
    if (context is Activity) return context
    context = context.baseContext
  }
  throw IllegalStateException("Not an Activity context!")
}
