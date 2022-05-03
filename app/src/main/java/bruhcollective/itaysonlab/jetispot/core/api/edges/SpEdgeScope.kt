package bruhcollective.itaysonlab.jetispot.core.api.edges

import bruhcollective.itaysonlab.jetispot.core.api.SpApiExecutor
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Message

interface SpEdgeScope {
  fun provideSpEdge(): SpApiExecutor.Edge
  fun provideSpExecutor(): SpApiExecutor
}

fun SpApiExecutor.Edge.scope(api: SpApiExecutor) = object: SpEdgeScope {
  override fun provideSpEdge() = this@scope
  override fun provideSpExecutor() = api
}

suspend inline fun <reified T> SpEdgeScope.getJson(suffix: String, params: Map<String, String> = mapOf()) = provideSpExecutor().getJson<T>(provideSpEdge(), suffix, params)
suspend inline fun <reified T : Message> SpEdgeScope.getProto(suffix: String, params: Map<String, String> = mapOf()) = provideSpExecutor().getProto<T>(provideSpEdge(), suffix, params)
suspend inline fun <reified In : Message, reified Out : Message> SpEdgeScope.postProto(suffix: String, body: In) = provideSpExecutor().postProto<In, Out>(provideSpEdge(), suffix, body)