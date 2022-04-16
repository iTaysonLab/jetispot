package bruhcollective.itaysonlab.jetispot.core

import android.content.Context
import bruhcollective.itaysonlab.jetispot.SpApp
import com.spotify.connectstate.Connect
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.gianlu.librespot.core.Session
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("BlockingMethodInNonBlockingContext")
@Singleton
class SpSessionManager @Inject constructor(
  @ApplicationContext private val appContext: Context,
) {
  private var _session: Session? = null
  val session get() = _session ?: throw IllegalStateException("Session is not created yet!")

  fun createSession() = Session.Builder(createCfg()).setDeviceType(Connect.DeviceType.SMARTPHONE).setDeviceName(DeviceIdProvider.getDeviceName(appContext)).setDeviceId(null).setPreferredLocale(Locale.getDefault().language)
  fun createCfg() = Session.Configuration.Builder().setCacheEnabled(false).setStoredCredentialsFile(File(appContext.filesDir, "spa_creds")).build()

  fun isSignedIn() = _session?.isValid == true
  fun setSession(s: Session) { _session = s }
}