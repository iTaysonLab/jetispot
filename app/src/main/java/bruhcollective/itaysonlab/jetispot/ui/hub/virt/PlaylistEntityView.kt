package bruhcollective.itaysonlab.jetispot.ui.hub.virt

import android.text.format.DateUtils
import bruhcollective.itaysonlab.jetispot.core.SpMetadataRequester
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.api.SpInternalApi
import bruhcollective.itaysonlab.jetispot.core.objs.hub.*
import bruhcollective.itaysonlab.jetispot.core.objs.player.*
import com.google.protobuf.ByteString
import com.google.protobuf.StringValue
import com.spotify.metadata.Metadata
import com.spotify.playlist4.Playlist4ApiProto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.ImageId
import xyz.gianlu.librespot.metadata.PlaylistId

object PlaylistEntityView {
  class ApiPlaylist(
    val playlist: Playlist4ApiProto.SelectedListContent,
    val playlistTrackMetadata: List<Playlist4ApiProto.Item>,
    val trackMetadata: Map<String, Metadata.Track>,
    val hubResponse: HubResponse
  )

  suspend fun getPlaylistView(id: String, sessionManager: SpSessionManager, spInternalApi: SpInternalApi, spMetadataRequester: SpMetadataRequester): ApiPlaylist {
    val playlist = withContext(Dispatchers.IO) { sessionManager.session.api().getPlaylist(PlaylistId.fromUri("spotify:playlist:$id")) }
    val playlistTracks = playlist.contents.itemsList.distinctBy { it.uri }.filter { it.uri.startsWith("spotify:track:") }
    val playlistOwnerUsername = "spotify:user:${playlist.ownerUsername}"

    val mappedMetadata = spMetadataRequester.request(mutableListOf(playlistOwnerUsername) + playlistTracks.map { it.uri })
    val mappedDuration = mappedMetadata.tracks.map { it.value.duration / 1000L }.sum()
    val playlistOwner = mappedMetadata.userProfiles[playlistOwnerUsername]!!
    val popCount = spInternalApi.getPlaylistPopCount(id)

    val playlistHeader = HubItem(
      component = if (playlist.attributes.formatAttributesList.firstOrNull { it.key == "image_url" } != null) HubComponent.LargePlaylistHeader else HubComponent.PlaylistHeader,
      text = HubText(
        title = playlist.attributes.name,
        subtitle = playlist.attributes.description
      ),
      custom = mapOf(
        "owner_name" to playlistOwner.name.value,
        "owner_pic" to (playlistOwner.imagesList.firstOrNull()?.url ?: ""),
        "owner_username" to playlistOwnerUsername,
        "total_duration" to DateUtils.formatElapsedTime(mappedDuration),
        "likes_count" to popCount.count
      ),
      children = listOf(
        HubItem(
          component = HubComponent.OutlinedButton,
          events = HubEvents(
            click = HubEvent.PlayFromContext(
              data = PlayFromContextData(
                "spotify:playlist:$id",
                PlayFromContextPlayerData(
                  context = PfcContextData(
                    url = "context://spotify:playlist:$id",
                    uri = "spotify:playlist:$id"
                  ),
                  state = PfcState(options = PfcStateOptions(shuffling_context = true)),
                  options = PfcOptions(player_options_override = PfcStateOptions(shuffling_context = true))
                )
              )
            )
          )
        )
      ),
      images = HubImages(
        HubImage(
          uri = playlist.attributes.formatAttributesList.find { it.key == "image" }?.value
            ?: playlist.attributes.formatAttributesList.find { it.key == "image_url" }?.value
            ?: playlist.attributes.pictureSizeList.find { it.targetName == "default" }?.url
            ?: if (playlist.attributes.hasPicture()) "https://i.scdn.co/image/${Utils.bytesToHex(playlist.attributes.picture).lowercase()}" else ""
        )
      )
    )

    val playlistItems = extensionResponseToHub(id, playlistTracks, mappedMetadata.tracks) // TODO attrs

    return ApiPlaylist(
      playlist = playlist,
      playlistTrackMetadata = playlistTracks,
      trackMetadata = mappedMetadata.tracks,
      hubResponse = HubResponse(
        header = playlistHeader,
        body = playlistItems
      )
    )
  }

  private fun extensionResponseToHub(
    playlistId: String,
    tracks: List<Playlist4ApiProto.Item>,
    mappedMetadata: Map<String, Metadata.Track>
  ): List<HubItem> {
    val hubItems = mutableListOf<HubItem>()
    tracks.forEach { trackItem ->
      val track = mappedMetadata[trackItem.uri]!!
      hubItems.add(
        HubItem(
          component = HubComponent.PlaylistTrackRow,
          id = trackItem.uri,
          text = HubText(
            title = track.name,
            subtitle = track.artistList.joinToString(", ") {
              it.name
            }
          ),
          custom = mapOf(
            "explicit" to track.explicit,
            "lyrics" to track.hasLyrics
          ),
          images = HubImages(
            main = HubImage(
              "https://i.scdn.co/image/${
                ImageId.fromHex(
                  Utils.bytesToHex(
                    track.album.coverGroup.imageList.find {
                      it.size == Metadata.Image.Size.SMALL
                    }?.fileId ?: ByteString.EMPTY
                  )
                ).hexId()
              }"
            )
          ),
          events = HubEvents(
            HubEvent.PlayFromContext(
              PlayFromContextData(
                trackItem.uri,
                PlayFromContextPlayerData(
                  PfcContextData(
                    url = "context://spotify:playlist:$playlistId",
                    uri = "spotify:playlist:$playlistId"
                  ),
                  PfcOptions(skip_to = PfcOptSkipTo(track_uri = trackItem.uri))
                )
              )
            )
          )
        )
      )
    }

    return hubItems
  }
}