package bruhcollective.itaysonlab.jetispot.ui.hub.virt

import bruhcollective.itaysonlab.jetispot.core.util.Log
import bruhcollective.itaysonlab.jetispot.core.SpSessionManager
import bruhcollective.itaysonlab.jetispot.core.objs.hub.*
import bruhcollective.itaysonlab.jetispot.core.objs.player.*
import com.spotify.extendedmetadata.ExtendedMetadata
import com.spotify.extendedmetadata.ExtensionKindOuterClass
import com.spotify.metadata.Metadata
import com.spotify.playlist4.Playlist4ApiProto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.ImageId
import xyz.gianlu.librespot.metadata.PlaylistId
import java.util.ArrayList

object PlaylistEntityView {
  class ApiPlaylist(
    val playlist: Playlist4ApiProto.SelectedListContent,
    val playlistTrackMetadata: List<Playlist4ApiProto.Item>,
    val trackMetadata: Map<String, Metadata.Track>,
    val hubResponse: HubResponse
  )

  suspend fun getPlaylistView(id: String, sessionManager: SpSessionManager): ApiPlaylist {
    val extensionQuery = ExtendedMetadata.ExtensionQuery.newBuilder().setExtensionKind(
      ExtensionKindOuterClass.ExtensionKind.TRACK_V4
    ).build()
    val entities: MutableList<ExtendedMetadata.EntityRequest> = ArrayList()

    // get tracks ids
    val playlist = withContext(Dispatchers.IO) { sessionManager.session.api().getPlaylist(
      PlaylistId.fromUri("spotify:playlist:$id")) }
    val playlistTracks = playlist.contents.itemsList

    val playlistHeader = HubItem(
      component = if (playlist.attributes.formatAttributesList.firstOrNull { it.key == "image_url" } != null) HubComponent.LargePlaylistHeader else HubComponent.PlaylistHeader,
      text = HubText(
        title = playlist.attributes.name,
        subtitle = playlist.attributes.description
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

    playlistTracks.listIterator().forEach { track ->
      entities.add(ExtendedMetadata.EntityRequest.newBuilder().apply {
        entityUri = track.uri
        addQuery(extensionQuery)
      }.build())
    }

    val playlistData = withContext(Dispatchers.IO) {
      sessionManager.session.api()
        .getExtendedMetadata(
          ExtendedMetadata.BatchedEntityRequest.newBuilder()
            .addAllEntityRequest(entities)
            .build()
        )
    }

    val mappedMetadata =
      playlistData.getExtendedMetadata(0).extensionDataList.associateBy { it.entityUri }
        .mapValues { Metadata.Track.parseFrom(it.value.extensionData.value) }

    val playlistItems = extensionResponseToHub(id, playlistTracks, mappedMetadata)

    return ApiPlaylist(
      playlist = playlist,
      playlistTrackMetadata = playlistTracks,
      trackMetadata = mappedMetadata,
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
          HubComponent.PlaylistTrackRow,
          id = track.gid.toStringUtf8(),
          text = HubText(
            title = track.name,
            subtitle = track.artistList.joinToString(", ") {
              it.name
            }
          ),
          images = HubImages(
            main = HubImage(
              "https://i.scdn.co/image/${
                ImageId.fromHex(
                  Utils.bytesToHex(
                    track.album.coverGroup.imageList.find {
                      it.size == Metadata.Image.Size.SMALL
                    }?.fileId!!
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