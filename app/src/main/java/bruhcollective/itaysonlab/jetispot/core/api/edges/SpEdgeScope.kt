package bruhcollective.itaysonlab.jetispot.core.api.edges

import bruhcollective.itaysonlab.jetispot.core.api.SpApiExecutor
import com.google.protobuf.GeneratedMessageV3

interface SpEdgeScope {
  fun provideSpEdge(): SpApiExecutor.Edge
  fun provideSpExecutor(): SpApiExecutor
}

fun SpApiExecutor.Edge.scope(api: SpApiExecutor) = object: SpEdgeScope {
  override fun provideSpEdge() = this@scope
  override fun provideSpExecutor() = api
}

suspend inline fun <reified T> SpEdgeScope.getJson(suffix: String, params: Map<String, String> = mapOf()) = provideSpExecutor().getJson<T>(provideSpEdge(), suffix, params)
suspend inline fun <reified T : GeneratedMessageV3> SpEdgeScope.getProto(suffix: String, params: Map<String, String> = mapOf()) = provideSpExecutor().getProto<T>(provideSpEdge(), suffix, params)