package bruhcollective.itaysonlab.jetispot.playback.service.library

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi
import androidx.media2.session.MediaSession
import bruhcollective.itaysonlab.jetispot.core.util.Log
import java.security.MessageDigest
import javax.inject.Inject

class SessionControllerVerifier @Inject constructor(
    private val packageManager: PackageManager
) {
    // public api

    fun verifyController(info: MediaSession.ControllerInfo): Boolean {
        checkedCache[info.packageName]?.let { entry ->
            if (entry.uid == info.uid) return entry.verified
        }

        val pkgInfo = packageSignature(info.packageName)

        if (pkgInfo.sha256Signature == null) {
            checkedCache[info.packageName] = AlreadyCheckedEntry(info.uid to false)
            return false
        }

        val isCallerKnown = when {
            info.uid == Process.myUid() -> true
            info.uid == Process.SYSTEM_UID -> true
            pkgInfo.sha256Signature == platformSignature -> true
            pkgInfo.activePermissions.contains(Manifest.permission.MEDIA_CONTENT_CONTROL) -> true
            pkgInfo.activePermissions.contains(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE) -> true
            else -> knownControllers[info.packageName]?.signatures?.firstOrNull { it.signature == pkgInfo.sha256Signature } != null
        }

        if (!isCallerKnown) {
            Log.w("SessionControllerVerifier", "Controller $info is not allowed")
        } else {
            Log.d("SessionControllerVerifier", "Controller $info is allowed")
        }

        checkedCache[info.packageName] = AlreadyCheckedEntry(info.uid to isCallerKnown)

        return isCallerKnown
    }

    // implementation

    private val checkedCache = mutableMapOf<String, AlreadyCheckedEntry>()

    private val knownControllers = mapOf(
        "com.google.android.projection.gearhead" to KnownControllerInfo(
            "Android Auto" to arrayOf(
                KnownSignature("AC:81:14:2D:AB:3C:B5:35:E5:55:C3:1B:17:43:D5:CA:4F:7C:79:6E:B6:F8:10:36:A3:8F:40:84:FF:16:66:9D" to false),
                KnownSignature("19:75:b2:f1:71:77:bc:89:a5:df:f3:1f:9e:64:a6:ca:e2:81:a5:3d:c1:d1:d5:9b:1d:14:7f:e1:c8:2a:fa:00" to false),
                KnownSignature("70:81:1a:3e:ac:fd:2e:83:e1:8d:a9:bf:ed:e5:2d:f1:6c:e9:1f:2e:69:a4:4d:21:f1:8a:b6:69:91:13:07:71" to false),
                KnownSignature("fd:b0:0c:43:db:de:8b:51:cb:31:2a:a8:1d:3b:5f:a1:77:13:ad:b9:4b:28:f5:98:d7:7f:8e:b8:9d:ac:ee:df" to true)
            )
        ),

        "com.google.android.wearable.app" to KnownControllerInfo(
            "WearOS" to arrayOf(
                KnownSignature("69:d0:72:16:9a:2c:6b:2f:5a:cc:59:0c:e4:33:a1:1a:c3:df:55:1a:df:ee:5d:5f:63:c0:83:b7:22:76:2e:19" to false),
                KnownSignature("85:cd:59:73:54:1b:e6:f4:77:d8:47:a0:bc:c6:aa:25:27:68:4b:81:9c:d5:96:85:29:66:4c:b0:71:57:b6:fe" to true)
            )
        ),

        "com.google.android.autosimulator" to KnownControllerInfo(
            "Android Auto Simulator" to arrayOf(
                KnownSignature("19:75:b2:f1:71:77:bc:89:a5:df:f3:1f:9e:64:a6:ca:e2:81:a5:3d:c1:d1:d5:9b:1d:14:7f:e1:c8:2a:fa:00" to true)
            )
        ),

        "com.google.android.googlequicksearchbox" to KnownControllerInfo(
            "Google" to arrayOf(
                KnownSignature("19:75:b2:f1:71:77:bc:89:a5:df:f3:1f:9e:64:a6:ca:e2:81:a5:3d:c1:d1:d5:9b:1d:14:7f:e1:c8:2a:fa:00" to false),
                KnownSignature("f0:fd:6c:5b:41:0f:25:cb:25:c3:b5:33:46:c8:97:2f:ae:30:f8:ee:74:11:df:91:04:80:ad:6b:2d:60:db:83" to true)
            )
        ),

        "com.google.android.carassistant" to KnownControllerInfo(
            "Google Assistant on Android Automotive OS" to arrayOf(
                KnownSignature("17:E2:81:11:06:2F:97:A8:60:79:7A:83:70:5B:F8:2C:7C:C0:29:35:56:6D:46:22:BC:4E:CF:EE:1B:EB:F8:15" to false),
                KnownSignature("74:B6:FB:F7:10:E8:D9:0D:44:D3:40:12:58:89:B4:23:06:A6:2C:43:79:D0:E5:A6:62:20:E3:A6:8A:BF:90:E2" to true)
            )
        ),
    )

    @JvmInline
    value class KnownControllerInfo (private val of: Pair<String, Array<KnownSignature>>) {
        val name get() = of.first
        val signatures get() = of.second
    }

    @JvmInline
    value class KnownSignature (private val of: Pair<String, Boolean>) {
        val signature get() = of.first
        val production get() = of.second
    }

    @JvmInline
    value class AlreadyCheckedEntry (private val of: Pair<Int, Boolean>) {
        val uid get() = of.first
        val verified get() = of.second
    }

    @JvmInline
    value class InstalledPackageInfo (private val of: Pair<List<String>, String?>) {
        val activePermissions get() = of.first
        val sha256Signature get() = of.second
    }

    private fun packageSignature(of: String) = packageManager.getPackageInfo(of, pmFlags() or PackageManager.GET_PERMISSIONS).let { packageInfo ->
        InstalledPackageInfo(packageInfo.activePermissions() to (if (pmNewSignaturesSupported()) packageInfo.sha256SignaturesModern() else packageInfo.sha256SignaturesLegacy())?.asSha256())
    }

    private fun ByteArray.asSha256(): String {
        return MessageDigest.getInstance("SHA256").also { it.update(this) }.digest().joinToString(":") { String.format("%02x", it) }
    }

    // platform layer implementation

    private val platformSignature by lazy { packageSignature("android").sha256Signature }

    private fun pmNewSignaturesSupported() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    @SuppressLint("PackageManagerGetSignatures")
    private fun pmFlags() = if (pmNewSignaturesSupported()) PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNING_CERTIFICATES

    @RequiresApi(Build.VERSION_CODES.P)
    private fun PackageInfo.sha256SignaturesModern() = this.signingInfo.signingCertificateHistory?.firstOrNull()?.toByteArray()

    @Suppress("DEPRECATION")
    private fun PackageInfo.sha256SignaturesLegacy() = this.signatures.firstOrNull()?.toByteArray()

    private fun PackageInfo.activePermissions() = requestedPermissions.mapIndexedNotNull { index, permission ->
        if ((requestedPermissionsFlags[index] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) permission else null
    }
}