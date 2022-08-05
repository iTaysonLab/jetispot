package bruhcollective.itaysonlab.jetispot.core.ext

import bruhcollective.itaysonlab.jetispot.core.util.SpUtils
import com.spotify.metadata.Metadata

val Metadata.Track.imageUrl: String? get() = SpUtils.getImageUrl(this.album.coverGroup.imageList.first { it.size == Metadata.Image.Size.DEFAULT }.fileId)