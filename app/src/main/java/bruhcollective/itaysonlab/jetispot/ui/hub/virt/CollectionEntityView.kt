package bruhcollective.itaysonlab.jetispot.ui.hub.virt

import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.collection.db.LocalCollectionDao
import bruhcollective.itaysonlab.jetispot.core.objs.hub.*
import bruhcollective.itaysonlab.jetispot.core.objs.player.*

object CollectionEntityView {
  suspend fun create(
    spSessionManager: SpSessionManager,
    spDao: LocalCollectionDao,
    sort: LocalCollectionDao.TrackSorts,
    invert: Boolean,
    tag: String?
  ): HubResponse {
    val uri = "spotify:user:${spSessionManager.session.username()}:collection"

    val hubBody = mutableListOf<HubItem>()
    var tags = spDao.getContentFilters()

    if (tag != null) tags = tags.filter { it.query == tag }

    val tracks = spDao.getTracks(tag?.removePrefix("tags contains "), sort, invert)

    tracks.forEach { track ->
      hubBody.add(HubItem(
        HubComponent.PlaylistTrackRow,
        id = track.id,
        text = HubText(
          title = track.name,
          subtitle = track.rawArtistsData.split("|").joinToString { it.split("=").getOrElse(1) { "" } } + " • " + track.albumName
        ),
        images = HubImages(
          main = HubImage(
            "https://i.scdn.co/image/${track.picture}"
          )
        ),
        events = HubEvents(
          HubEvent.PlayFromContext(
            PlayFromContextData(
              track.uri,
              PlayFromContextPlayerData(
                PfcContextData(
                  url = "context://$uri",
                  uri = uri
                ),
                PfcOptions(skip_to = PfcOptSkipTo(track_uri = track.uri))
              )
            )
          )
        )
      ))
    }

    return HubResponse(
      header = HubItem(
        HubComponent.CollectionHeader,
        custom = mapOf(
          "count" to tracks.size,
          "cfr" to tags,
          "cfr_cur" to (tag ?: "")
        )
      ),
      body = hubBody
    )
  }
}