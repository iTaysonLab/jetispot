package bruhcollective.itaysonlab.jetispot.core

import android.content.Context
import android.os.Build
import android.provider.Settings

object DeviceIdProvider {
    const val SPOTIFY_APP_VERSION = "8.7.20.1261"

    fun getDeviceName(appContext: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val deviceName = Settings.Global.getString(appContext.contentResolver, Settings.Global.DEVICE_NAME)
            if (deviceName == Build.MODEL) Build.MODEL else "$deviceName (${Build.MODEL})"
        } else {
            Build.MODEL
        }
    }
}