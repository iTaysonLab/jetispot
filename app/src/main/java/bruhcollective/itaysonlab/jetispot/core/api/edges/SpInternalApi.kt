package bruhcollective.itaysonlab.jetispot.core.api.edges

import android.util.Log
import bruhcollective.itaysonlab.jetispot.core.api.SpApiExecutor
import bruhcollective.itaysonlab.jetispot.core.objs.hub.*
import bruhcollective.itaysonlab.jetispot.core.objs.player.PfcContextData
import bruhcollective.itaysonlab.jetispot.core.objs.player.PfcOptions
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextData
import bruhcollective.itaysonlab.jetispot.core.objs.player.PlayFromContextPlayerData
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
) {
    suspend fun getHomeView() = api.getJson<HubResponse>(
        SpApiExecutor.Edge.Internal, "/homeview/v1/home", mapOf(
            "platform" to "android",
            "client-timezone" to TimeZone.getDefault().id,
            "locale" to api.sessionManager.session.preferredLocale(),
            "video" to "true",
            "podcast" to "true",
            "is_car_connected" to "false"
        )
    )

    suspend fun getBrowseView(pageId: String = "") = api.getJson<HubResponse>(
        SpApiExecutor.Edge.Internal, "/hubview-mobile-v1/browse/$pageId", mapOf(
            "platform" to "android",
            "client-timezone" to TimeZone.getDefault().id,
            "locale" to api.sessionManager.session.preferredLocale(),
            "podcast" to "true"
        )
    )

    suspend fun getAlbumView(id: String = "") = api.getJson<HubResponse>(
        SpApiExecutor.Edge.Internal, "/album-entity-view/v2/album/$id", mapOf(
            "platform" to "android",
            "client-timezone" to TimeZone.getDefault().id,
            "locale" to api.sessionManager.session.preferredLocale(),
            "video" to "true",
            "podcast" to "true",
            "application" to "nft",
            "checkDeviceCapability" to "true"
        )
    )

    suspend fun getArtistView(id: String = "") = api.getJson<HubResponse>(
        SpApiExecutor.Edge.Internal, "/artistview/v1/artist/$id", mapOf(
            "platform" to "android",
            "client-timezone" to TimeZone.getDefault().id,
            "locale" to api.sessionManager.session.preferredLocale(),
            "podcast" to "true",
            "video" to "true",
            "purchase_allowed" to "false",
            "timeFormat" to "24h"
        )
    )

    suspend fun getPlaylistView(id: String): HubResponse {
        val extensionQuery = ExtensionQuery.newBuilder().setExtensionKind(
            ExtensionKind.TRACK_V4
        ).build()
        val entities: MutableList<EntityRequest> = ArrayList()

        // get tracks ids
        val playlist = getPlaylist(id)
        val playlistTracks = playlist.contents.itemsList

        Log.d("SPINTERNAL", playlist.attributes.unknownFields.asMap()[13]
            ?.lengthDelimitedList
            ?.get(0)?.toStringUtf8()!!
        )

        val playlistHeader = HubItem(
            component = HubComponent.PlaylistHeader,
            text = HubText(
                title = playlist.attributes.name,
                subtitle = playlist.attributes.description
            ),
            images = HubImages(
                HubImage(
                    uri = playlist.attributes.formatAttributesList.find { it.key == "image" }?.value
                        ?: playlist.attributes.unknownFields.asMap()[13]
                            ?.lengthDelimitedList
                            ?.get(0)?.toStringUtf8()
                                // I HAVE NO IDEA ABOUT THIS DON'T TOUCH
                            ?.split(Regex(".default.."))?.get(1)
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
        val playlistItems = extensionResponseToHub(playlistData)

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

    private fun extensionResponseToHub(response: BatchedExtensionResponse): List<HubItem> {
        val hubItems: MutableList<HubItem> = ArrayList()
        for (data in response.getExtendedMetadata(0).extensionDataList) {
            val track = Metadata.Track.parseFrom(data.extensionData.value)
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
                                data.entityUri,
                                PlayFromContextPlayerData(
                                    PfcContextData(uri = data.entityUri),
                                    PfcOptions()
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
