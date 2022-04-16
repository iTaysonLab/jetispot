package bruhcollective.itaysonlab.jetispot.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import xyz.gianlu.librespot.mercury.MercuryClient
import java.util.*

object DeviceIdProvider {
    @Volatile
    var cachedDeviceId: String = ""

    @Synchronized
    fun getDeviceId(context: Context): String {
        synchronized(DeviceIdProvider::class.java) {
            try {
                if (cachedDeviceId.isNotEmpty()) return cachedDeviceId

                //cachedDeviceId = AuthPreferences.deviceID
                if (cachedDeviceId.isEmpty()) {
                    cachedDeviceId = generateUuid(context)
                    //AuthPreferences.deviceID = cachedDeviceId
                }

                return cachedDeviceId
            } catch (ignored: Exception) {
                return ""
            }
        }
    }

    @SuppressLint("HardwareIds")
    fun generateUuid(context: Context): String {
        val d = Settings.Secure.getString(context.contentResolver, "android_id")
        return if (TextUtils.isEmpty(d)) {
            UUID.randomUUID().toString()
        } else d
    }

    fun getDeviceName(appContext: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val deviceName = Settings.Global.getString(appContext.contentResolver, Settings.Global.DEVICE_NAME)
            if (deviceName == Build.MODEL) Build.MODEL else "$deviceName (${Build.MODEL})"
        } else {
            Build.MODEL
        }
    }
}