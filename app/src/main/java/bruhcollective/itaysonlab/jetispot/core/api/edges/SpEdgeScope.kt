package bruhcollective.itaysonlab.jetispot.core.api.edges

import bruhcollective.itaysonlab.jetispot.core.api.SpApiExecutor

interface SpEdgeScope {
  fun provideSpEdge(): SpApiExecutor.Edge
  fun provideSpExecutor(): SpApiExecutor
}

fun SpApiExecutor.Edge.scope(api: SpApiExecutor) = object: SpEdgeScope {
  override fun provideSpEdge() = this@scope
  override fun provideSpExecutor() = api
}

suspend inline fun <reified T> SpEdgeScope.getJson(suffix: String, params: Map<String, String>) = provideSpExecutor().getJson<T>(provideSpEdge(), suffix, params)