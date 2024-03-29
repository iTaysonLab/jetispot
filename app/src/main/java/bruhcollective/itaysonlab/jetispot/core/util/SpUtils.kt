package bruhcollective.itaysonlab.jetispot.core.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.protobuf.ByteString
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.ImageId

object SpUtils {
    const val SPOTIFY_APP_VERSION = "8.7.68.568"

    fun getDeviceName(appContext: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val deviceName = Settings.Global.getString(appContext.contentResolver, Settings.Global.DEVICE_NAME)
            if (deviceName == Build.MODEL) Build.MODEL else "$deviceName (${Build.MODEL})"
        } else {
            Build.MODEL
        }
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }

    fun getScannableUrl(uri: String) = "https://scannables.scdn.co/uri/800/$uri"
    fun getImageUrl(bytes: ByteString?) = if (bytes != null) "https://i.scdn.co/image/${Utils.bytesToHex(bytes).lowercase()}" else null
}