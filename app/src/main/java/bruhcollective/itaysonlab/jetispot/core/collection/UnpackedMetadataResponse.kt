package bruhcollective.itaysonlab.jetispot.core.collection

import com.spotify.descriptorextension.proto.ExtensionDescriptorData
import com.spotify.extendedmetadata.ExtendedMetadata
import com.spotify.extendedmetadata.ExtensionKindOuterClass
import com.spotify.identity.proto.v3.IdentityV3
import com.spotify.metadata.Metadata

class UnpackedMetadataResponse(
  dataArray: List<ExtendedMetadata.EntityExtensionDataArray>
) {
  var tracks: MutableMap<String, Metadata.Track> = mutableMapOf()
    private set

  var artists: MutableMap<String, Metadata.Artist> = mutableMapOf()
    private set

  var albums: MutableMap<String, Metadata.Album> = mutableMapOf()
    private set

  var descriptors: MutableMap<String, ExtensionDescriptorData> = mutableMapOf()
    private set

  var userProfiles: MutableMap<String, IdentityV3.UserProfile> = mutableMapOf()
    private set

  infix operator fun plusAssign (other: UnpackedMetadataResponse) {
    tracks += other.tracks
    artists += other.artists
    albums += other.albums
    descriptors += other.descriptors
  }

  init {
    dataArray.forEach { arr ->
      when (arr.extensionKind) {
        ExtensionKindOuterClass.ExtensionKind.TRACK_V4 -> {
          tracks += arr.extensionDataList.associate {
            Pair(
              it.entityUri,
              Metadata.Track.parseFrom(it.extensionData.value)
            )
          }
        }

        ExtensionKindOuterClass.ExtensionKind.ALBUM_V4 -> {
          albums += arr.extensionDataList.associate {
            Pair(
              it.entityUri,
              Metadata.Album.parseFrom(it.extensionData.value)
            )
          }
        }

        ExtensionKindOuterClass.ExtensionKind.ARTIST_V4 -> {
          artists += arr.extensionDataList.associate {
            Pair(
              it.entityUri,
              Metadata.Artist.parseFrom(it.extensionData.value)
            )
          }
        }

        ExtensionKindOuterClass.ExtensionKind.TRACK_DESCRIPTOR -> {
          descriptors += arr.extensionDataList.associate {
            Pair(
              it.entityUri,
              ExtensionDescriptorData.parseFrom(it.extensionData.value)
            )
          }
        }

        ExtensionKindOuterClass.ExtensionKind.USER_PROFILE -> {
          userProfiles += arr.extensionDataList.associate {
            Pair(
              it.entityUri,
              IdentityV3.UserProfile.parseFrom(it.extensionData.value)
            )
          }
        }
      }
    }
  }
}