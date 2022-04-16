package bruhcollective.itaysonlab.jetispot.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.gianlu.librespot.core.Session
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpAuthManager @Inject constructor(
  private val spSessionManager: SpSessionManager,
  private val spPlayerManager: SpPlayerManager
) {
  @Suppress("BlockingMethodInNonBlockingContext")
  suspend fun authWith(username: String, password: String) = withContext(Dispatchers.IO) {
    try {
      spSessionManager.setSession(spSessionManager.createSession().userPass(username, password).create())
      spPlayerManager.createPlayer()
      AuthResult.Success
    } catch (se: Session.SpotifyAuthenticationException) {
      AuthResult.SpError(se.message ?: "Unknown error")
    } catch (e: Exception) {
      e.printStackTrace()
      AuthResult.Exception(e)
    }
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  suspend fun authStored() = withContext(Dispatchers.IO) {
    try {
      spSessionManager.setSession(spSessionManager.createSession().stored().create())
      spPlayerManager.createPlayer()
    } catch (e: Exception) {}
  }

  sealed class AuthResult {
    object Success: AuthResult()
    class SpError(val msg: String): AuthResult()
    class Exception(val e: kotlin.Exception): AuthResult()
  }
}