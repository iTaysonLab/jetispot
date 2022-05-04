package bruhcollective.itaysonlab.jetispot.ui.ext

import com.google.protobuf.Any
import com.google.protobuf.Message

fun Any.dynamicUnpack() = unpack(Class.forName(typeUrl.split("/")[1].let {
  if (!it.startsWith("com.spotify")) "com.spotify.${it}" else it
}) as Class<out Message>)