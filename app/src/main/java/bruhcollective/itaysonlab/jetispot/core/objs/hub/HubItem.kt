package bruhcollective.itaysonlab.jetispot.core.objs.hub

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class HubItem (
  val component: HubComponent,
  val id: String = UUID.randomUUID().toString(),
  // types
  val text: HubText? = null,
  val children: List<HubItem>? = null,
  val images: HubImages? = null,
  val events: HubEvents? = null,
  //
  val custom: Map<String, Any>? = null,
  val metadata: HubItemMetadata? = null
)

//

@JsonClass(generateAdapter = true)
class HubText (
  val title: String? = null,
  val subtitle: String? = null,
  val description: String? = null
)

@JsonClass(generateAdapter = true)
class HubImages (
  val main: HubImage? = null,
  val background: HubImage? = null,
)

@JsonClass(generateAdapter = true)
class HubImage (
  val uri: String? = null,
  val placeholder: String? = null,
  val custom: HubImageCustom? = null
) {
  val isRounded = custom?.style == "rounded"
}

@JsonClass(generateAdapter = true)
class HubImageCustom (
  val style: String? = null
)

@JsonClass(generateAdapter = true)
class HubEvents (
  val click: HubEvent?
)

@JsonClass(generateAdapter = true)
class NavigateUri (
  val uri: String
)

@JsonClass(generateAdapter = true)
class HubItemMetadata (
  val uri: String? = null,
  val album: JsonAlbum? = null
)

@JsonClass(generateAdapter = true)
class JsonAlbum (
  val year: Int,
  val artists: List<JsonArtist>,
  val type: String
)

@JsonClass(generateAdapter = true)
class JsonArtist (
  val name: String,
  val uri: String,
  val images: List<JsonImage>
)

@JsonClass(generateAdapter = true)
class JsonImage (
  val uri: String,
  val width: Int,
  val height: Int
)