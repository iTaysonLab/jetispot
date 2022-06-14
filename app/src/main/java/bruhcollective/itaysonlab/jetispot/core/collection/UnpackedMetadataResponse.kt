package bruhcollective.itaysonlab.jetispot.core.collection

import com.google.protobuf.ByteString
import com.spotify.descriptorextension.proto.ExtensionDescriptorData
import com.spotify.extendedmetadata.EntityExtensionDataOuterClass
import com.spotify.extendedmetadata.ExtendedMetadata
import com.spotify.extendedmetadata.ExtensionKindOuterClass
import com.spotify.identity.proto.v3.IdentityV3
import com.spotify.metadata.Metadata
import com.spotify.podcastcreatorinteractivity.v1.PodcastRating
import com.spotify.podcastextensions.proto.PodcastTopics

class UnpackedMetadataResponse(
  dataArray: List<ExtendedMetadata.EntityExtensionDataArray>
) {
  var tracks: StringMap<Metadata.Track> = mutableMapOf()
    private set

  var artists: StringMap<Metadata.Artist> = mutableMapOf()
    private set

  var albums: StringMap<Metadata.Album> = mutableMapOf()
    private set

  var descriptors: StringMap<ExtensionDescriptorData> = mutableMapOf()
    private set

  var userProfiles: StringMap<IdentityV3.UserProfile> = mutableMapOf()
    private set

  var shows: StringMap<Metadata.Show> = mutableMapOf()
    private set

  var episodes: StringMap<Metadata.Episode> = mutableMapOf()
    private set

  var podcastTopics: StringMap<PodcastTopics> = mutableMapOf()
    private set

  var podcastRatings: StringMap<PodcastRating> = mutableMapOf()
    private set

  init {
    dataArray.forEach { arr ->
      when (arr.extensionKind) {
        ExtensionKindOuterClass.ExtensionKind.TRACK_V4 -> tracks += arr.extensionDataList dataPair { Metadata.Track.parseFrom(it) }
        ExtensionKindOuterClass.ExtensionKind.ALBUM_V4 -> albums += arr.extensionDataList dataPair { Metadata.Album.parseFrom(it) }
        ExtensionKindOuterClass.ExtensionKind.ARTIST_V4 -> artists += arr.extensionDataList dataPair { Metadata.Artist.parseFrom(it) }
        ExtensionKindOuterClass.ExtensionKind.TRACK_DESCRIPTOR -> descriptors += arr.extensionDataList dataPair { ExtensionDescriptorData.parseFrom(it) }
        ExtensionKindOuterClass.ExtensionKind.USER_PROFILE -> userProfiles += arr.extensionDataList dataPair { IdentityV3.UserProfile.parseFrom(it) }
        ExtensionKindOuterClass.ExtensionKind.SHOW_V4 -> shows += arr.extensionDataList dataPair { Metadata.Show.parseFrom(it) }
        ExtensionKindOuterClass.ExtensionKind.EPISODE_V4 -> episodes += arr.extensionDataList dataPair { Metadata.Episode.parseFrom(it) }
        ExtensionKindOuterClass.ExtensionKind.PODCAST_TOPICS -> podcastTopics += arr.extensionDataList dataPair { PodcastTopics.parseFrom(it) }
        ExtensionKindOuterClass.ExtensionKind.PODCAST_RATING -> podcastRatings += arr.extensionDataList dataPair { PodcastRating.parseFrom(it) }
      }
    }
  }

  infix operator fun plusAssign (other: UnpackedMetadataResponse) {
    tracks += other.tracks
    artists += other.artists
    albums += other.albums
    descriptors += other.descriptors
    userProfiles += other.userProfiles
    shows += other.shows
    episodes += other.episodes
    podcastTopics += other.podcastTopics
    podcastRatings += other.podcastRatings
  }

  private inline infix fun <T> List<EntityExtensionDataOuterClass.EntityExtensionData>.dataPair(crossinline parse: (ByteString) -> T) = associate {
    it.entityUri to parse(it.extensionData.value)
  }
}

typealias StringMap<T> = MutableMap<String, T>