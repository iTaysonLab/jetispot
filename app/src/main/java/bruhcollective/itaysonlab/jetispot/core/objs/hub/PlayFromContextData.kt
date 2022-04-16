package bruhcollective.itaysonlab.jetispot.core.objs.hub

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PlayFromContextData (
  val uri: String,
  val player: PlayFromContextPlayerData
)

@JsonClass(generateAdapter = true)
class PlayFromContextPlayerData (
  val context: PfcContextData,
  val options: PfcOptions
)

@JsonClass(generateAdapter = true)
class PfcContextData (
  val url: String? = null,
  val uri: String
)

@JsonClass(generateAdapter = true)
class PfcOptions (
  val skip_to: PfcOptSkipTo? = null
)

@JsonClass(generateAdapter = true)
class PfcOptSkipTo (
  val page_index: Int? = null,
  val track_uri: String
)