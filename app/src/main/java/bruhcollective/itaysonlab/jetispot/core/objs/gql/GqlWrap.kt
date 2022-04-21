package bruhcollective.itaysonlab.jetispot.core.objs.gql

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GqlWrap <T> (
  val data: T
)