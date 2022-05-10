package bruhcollective.itaysonlab.jetispot.core.collection

import com.spotify.metadata.Metadata
import com.spotify.extendedmetadata.ExtendedMetadata
import com.spotify.extendedmetadata.ExtensionKindOuterClass

class UnpackedMetadataResponse(
  dataArray: List<ExtendedMetadata.EntityExtensionDataArray>
) {
  var tracks: Map<String, Metadata.Track> = mapOf()
    private set

  var artists: Map<String, Metadata.Artist> = mapOf()
    private set

  var albums: Map<String, Metadata.Album> = mapOf()
    private set

  init {
    dataArray.forEach { arr ->
      when (arr.extensionKind) {
        ExtensionKindOuterClass.ExtensionKind.TRACK_V4 -> {
          tracks = arr.extensionDataList.associate {
            Pair(
              it.entityUri,
              Metadata.Track.parseFrom(it.extensionData.value)
            )
          }
        }

        ExtensionKindOuterClass.ExtensionKind.ALBUM_V4 -> {
          albums = arr.extensionDataList.associate {
            Pair(
              it.entityUri,
              Metadata.Album.parseFrom(it.extensionData.value)
            )
          }
        }

        ExtensionKindOuterClass.ExtensionKind.ARTIST_V4 -> {
          artists = arr.extensionDataList.associate {
            Pair(
              it.entityUri,
              Metadata.Artist.parseFrom(it.extensionData.value)
            )
          }
        }
      }
    }
  }
}