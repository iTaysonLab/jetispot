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
    val showMetadata = metadataRequester.request {
      add(
        "spotify:show:$id" to listOf(
          ExtensionKindOuterClass.ExtensionKind.SHOW_V4,
          ExtensionKindOuterClass.ExtensionKind.PODCAST_TOPICS,
        )
      )
    }

    val show = showMetadata.shows[id]!!
    val topics = showMetadata.podcastTopics[id]!!

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
        add(HubItem(
          component = HubComponent.PodcastTopics,
          custom = mapOf("topics" to topics)
        ))

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