package bruhcollective.itaysonlab.jetispot.ui.hub.virt

import bruhcollective.itaysonlab.jetispot.core.SpMetadataRequester
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.episodes
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubComponent
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubResponse
import com.spotify.extendedmetadata.ExtensionKindOuterClass
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.EpisodeId

object ShowEntityView {
  suspend fun create(
    spSessionManager: SpSessionManager,
    metadataRequester: SpMetadataRequester,
    id: String
  ): HubResponse {
    val spId = "spotify:show:$id"

    val showMetadata = metadataRequester.request {
      add(
        spId to listOf(
          ExtensionKindOuterClass.ExtensionKind.SHOW_V4,
          ExtensionKindOuterClass.ExtensionKind.PODCAST_TOPICS,
          ExtensionKindOuterClass.ExtensionKind.PODCAST_RATING,
        )
      )
    }

    val show = showMetadata.shows[spId]!!
    val topics = showMetadata.podcastTopics[spId]!!
    val ratings = showMetadata.podcastRatings[spId]!!

    val episodeMetadata = metadataRequester.request {
      episodes(show.episodeList.map { EpisodeId.fromHex(Utils.bytesToHex(it.gid)).toSpotifyUri() })
    }

    return HubResponse(
      title = show.name,
      header = HubItem(
        component = HubComponent.ShowHeader,
        custom = mapOf("show" to show)
      ),
      body = buildList {
        add(
          HubItem(
            component = HubComponent.PodcastTopics,
            custom = mapOf("topics" to topics, "ratings" to ratings)
          )
        )

        addAll(
          show.episodeList.map {
            val sid = EpisodeId.fromHex(Utils.bytesToHex(it.gid)).toSpotifyUri()
            val episode = episodeMetadata.episodes[sid]!!

            HubItem(
              component = HubComponent.EpisodeListItem,
              custom = mapOf("episode" to episode)
            )
          }
        )
      }
    )
  }
}