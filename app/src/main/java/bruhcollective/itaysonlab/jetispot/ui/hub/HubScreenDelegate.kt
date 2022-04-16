package bruhcollective.itaysonlab.jetispot.ui.hub

import bruhcollective.itaysonlab.jetispot.core.objs.hub.PlayFromContextData

interface HubScreenDelegate {
  fun play(data: PlayFromContextData)
}