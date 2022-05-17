package bruhcollective.itaysonlab.jetispot.core.objs.tags

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ContentFilterResponse(
  val contentFilters: List<ContentFilter>
)

@JsonClass(generateAdapter = true)
class ContentFilter(
  val title: String,
  val query: String
)