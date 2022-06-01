package bruhcollective.itaysonlab.jetispot.ui.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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