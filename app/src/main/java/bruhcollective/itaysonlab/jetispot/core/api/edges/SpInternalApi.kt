package bruhcollective.itaysonlab.jetispot.core.api.edges

import android.util.Log
import bruhcollective.itaysonlab.jetispot.core.api.SpApiExecutor
import bruhcollective.itaysonlab.jetispot.core.objs.hub.*
import bruhcollective.itaysonlab.jetispot.core.objs.player.*
import com.spotify.extendedmetadata.ExtendedMetadata.*
import com.spotify.extendedmetadata.ExtensionKindOuterClass.ExtensionKind
import com.spotify.metadata.Metadata
import com.spotify.playlist4.Playlist4ApiProto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.gianlu.librespot.common.Utils
import xyz.gianlu.librespot.metadata.ImageId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpInternalApi @Inject constructor(
    private val api: SpApiExecutor
): SpEdgeScope by SpApiExecutor.Edge.Internal.scope(api) {
    suspend fun getHomeView() = getJson<HubResponse>(
        "/homeview/v1/home", mapOf("is_car_connected" to "false")
    )

    suspend fun getBrowseView(pageId: String = "") = getJson<HubResponse>(
        "/hubview-mobile-v1/browse/$pageId", mapOf()
    )

    suspend fun getAlbumView(id: String = "") = getJson<HubResponse>(
        "/album-entity-view/v2/album/$id", mapOf("checkDeviceCapability" to "true")
    )

    suspend fun getArtistView(id: String = "") = getJson<HubResponse>(
        "/artistview/v1/artist/$id", mapOf(
            "purchase_allowed" to "false",
            "timeFormat" to "24h"
        )
    )

    suspend fun getReleasesView(id: String = "") = getJson<HubResponse>(
        "/artistview/v1/artist/$id/releases", mapOf("checkDeviceCapability" to "true")
    )

    suspend fun getPlaylistView(id: String): HubResponse {
        val extensionQuery = ExtensionQuery.newBuilder().setExtensionKind(
            ExtensionKind.TRACK_V4
        ).build()
        val entities: MutableList<EntityRequest> = ArrayList()

        // get tracks ids
        val playlist = getPlaylist(id)
        val playlistTracks = playlist.contents.itemsList

        //Log.d("SCM", playlist.toString())

        val playlistHeader = HubItem(
            component = if (playlist.attributes.formatAttributesList.firstOrNull { it.key == "image_url" } != null) HubComponent.LargePlaylistHeader else HubComponent.PlaylistHeader,
            text = HubText(
                title = playlist.attributes.name,
                subtitle = playlist.attributes.description
            ),
            images = HubImages(
                HubImage(
                    uri = playlist.attributes.formatAttributesList.find { it.key == "image" }?.value
                        ?: playlist.attributes.formatAttributesList.find { it.key == "image_url" }?.value
                        ?: "https://i.scdn.co/image/${ImageId.fromHex(Utils.bytesToHex(playlist.attributes.picture)).hexId()}"
                )
            )
        )

        playlistTracks.listIterator().forEach { track ->
            entities.add(EntityRequest.newBuilder().apply {
                entityUri = track.uri
                addQuery(extensionQuery)
            }.build())
        }

        val playlistData = withContext(Dispatchers.IO) {
            api.sessionManager.session.api()
                .getExtendedMetadata(
                    BatchedEntityRequest.newBuilder()
                        .addAllEntityRequest(entities)
                        .build()
                )
        }

        val playlistItems = extensionResponseToHub(id, playlistTracks, playlistData)

        return HubResponse(
            header = playlistHeader,
            body = playlistItems
        )
    }

    private suspend fun getPlaylist(id: String): Playlist4ApiProto.SelectedListContent {
        val response = api.get<Playlist4ApiProto.SelectedListContent>(
            SpApiExecutor.Edge.Internal, "/playlist/v2/playlist/$id", mapOf(
                "platform" to "android",
                "client-timezone" to TimeZone.getDefault().id,
                "locale" to api.sessionManager.session.preferredLocale()
            )
        ) { response ->
            Playlist4ApiProto.SelectedListContent.parseFrom(response.body!!.byteStream())
        }

        return response
    }

    private fun extensionResponseToHub(playlistId: String, tracks: List<Playlist4ApiProto.Item>, response: BatchedExtensionResponse): List<HubItem> {
        val hubItems = mutableListOf<HubItem>()
        val mappedMetadata = response.getExtendedMetadata(0).extensionDataList.associateBy { it.entityUri }.mapValues { Metadata.Track.parseFrom(it.value.extensionData.value) }

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
                                    PfcContextData(url = "context://spotify:playlist:$playlistId", uri = "spotify:playlist:$playlistId"),
                                    PfcOptions(skip_to = PfcOptSkipTo(track_uri = trackItem.uri))
                                )
                            )
                        )
                    )
                )
            )
        }

        for (data in response.getExtendedMetadata(0).extensionDataList) {

        }

        return hubItems
    }
}
