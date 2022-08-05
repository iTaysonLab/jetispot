package bruhcollective.itaysonlab.jetispot.playback.service.library

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.os.bundleOf
import androidx.media.utils.MediaConstants
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.session.LibraryResult
import androidx.media2.session.MediaLibraryService
import androidx.media2.session.MediaSession
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.SpMetadataRequester
import bruhcollective.itaysonlab.jetispot.core.api.SpExternalIntegrationApi
import bruhcollective.itaysonlab.jetispot.core.collection.SpCollectionManager
import bruhcollective.itaysonlab.jetispot.core.objs.external.PersonalizedRecommendationsAccessory
import bruhcollective.itaysonlab.jetispot.core.objs.external.PersonalizedRecommendationsRequest
import bruhcollective.itaysonlab.jetispot.core.util.Log
import bruhcollective.itaysonlab.jetispot.playback.helpers.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

@SuppressLint("RestrictedApi")
class MediaLibraryConnector @Inject constructor(
    private val spMetadataRequester: SpMetadataRequester,
    private val spCollectionManager: SpCollectionManager,
    private val spExternalIntegrationApi: SpExternalIntegrationApi
) {
    companion object {
        private val tabs = listOf(
            Triple(R.string.auto_tabs_home, R.drawable.round_home_white_24, "jetispot:autoHome"),
            Triple(R.string.auto_tabs_recent, R.drawable.round_history_white_24, "jetispot:autoRecent"),
            Triple(R.string.auto_tabs_browse, R.drawable.round_assistant_white_24, "jetispot:autoBrowse"),
            Triple(R.string.auto_tabs_library, R.drawable.round_library_music_white_24, "jetispot:autoLibrary")
        )
    }

    // API

    fun root(
        controller: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult {
        Log.d("MediaLibraryConnector", "> root $controller $params")

        val rootUrl = when (controller.packageName) {
            "com.google.android.projection.gearhead", "com.google.android.autosimulator" -> "jetispot:autoRoot"
            else -> "jetispot:emptyRoot"
        }

        return LibraryResult(LibraryResult.RESULT_SUCCESS, mediaItem { id = rootUrl }, null)
    }

    suspend fun load(serviceCtx: Context, url: String, page: Int, pageSize: Int): LibraryResult = when (url) {
        "jetispot:autoRoot" -> {
            LibraryResult(LibraryResult.RESULT_SUCCESS, tabs.map {
                mediaItem {
                    id = it.third
                    title = serviceCtx.getString(it.first)
                    iconBitmap = BitmapFactory.decodeResource(serviceCtx.resources, it.second)

                    if (it.third == "jetispot:autoHome") {
                        extras(
                            MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_BROWSABLE to MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
                            MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_PLAYABLE to MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
                        )
                    }

                    browsable()
                }
            }, null)
        }

        "jetispot:autoHome" -> {
            LibraryResult(LibraryResult.RESULT_SUCCESS, spExternalIntegrationApi.personalizedRecommendations(PersonalizedRecommendationsRequest(
                accessory = PersonalizedRecommendationsAccessory.Auto,
                signals = emptyList(),
                dateTime = Clock.System.now().toString().removeSuffix("Z")
            )).content.flatMap { section ->
                section.items.map { item ->
                    mediaItem {
                        id = item.uri
                        title = item.title
                        subtitle = item.subtitle
                        iconUri = item.image
                        browsable()
                        extras(
                            MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_GROUP_TITLE to section.title,
                            MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_BROWSABLE to MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
                            MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_PLAYABLE to MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
                        )
                    }
                }
            }, MediaLibraryService.LibraryParams.Builder().setExtras(bundleOf(
                MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_BROWSABLE to MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
                MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_PLAYABLE to MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM
            )).build())
        }

        else -> LibraryResult(LibraryResult.RESULT_SUCCESS, emptyList(), null)
    }
}