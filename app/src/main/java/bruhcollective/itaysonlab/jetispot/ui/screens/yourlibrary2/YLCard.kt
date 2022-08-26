package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Podcasts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.*
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import bruhcollective.itaysonlab.jetispot.ui.shared.ImagePreview
import bruhcollective.itaysonlab.jetispot.ui.shared.MarqueeText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import coil.compose.AsyncImage

@Composable
fun YLCardRender(
  item: CollectionEntry,
  modifier: Modifier
){
  when (item) {
    is CollectionPinnedItem -> YLPinnedCard(item, modifier)
    is CollectionRootlistItem -> YLRCardRootlist(item = item, modifier = modifier)
    is CollectionAlbum -> YLRCardAlbum(item = item, modifier = modifier)
    is CollectionArtist -> YLRCardArtist(item = item, modifier = modifier)
    else -> Text(item.toString())
  }
}


@Composable
fun YLRCardRootlist(
  item: CollectionRootlistItem,
  modifier: Modifier
) {
  YLAlbumCard(
    picUrl = item.picture,
    picPlaceholder = "playlist",
    title = item.name,
    subtitle = item.ownerUsername,
    modifier = modifier
  )
}

@Composable
fun YLRCardAlbum(
  item: CollectionAlbum,
  modifier: Modifier
) {
  YLAlbumCard(
    picUrl = "https://i.scdn.co/image/${item.picture}",
    picPlaceholder = "album",
    title = item.name,
    subtitle = item.rawArtistsData.split("|").joinToString { it.split("=")[1] },
    modifier = modifier
  )
}

@Composable
fun YLRCardArtist(
  item: CollectionArtist,
  modifier: Modifier
) {
  YLArtistCard(
    picUrl = "https://i.scdn.co/image/${item.picture}",
    picPlaceholder = "artist",
    title = item.name,
    modifier = modifier
  )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun YLPinnedCard(
  item: CollectionPinnedItem,
  modifier: Modifier
) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
    shape = RoundedCornerShape(24.dp),
    modifier = Modifier
      .height(264.dp)
      .width(164.dp)
  ) {
    Column(
      modifier = modifier.padding(14.dp)
    ) {
      Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)) {
        val isPredef = item.predefType != null
        if (isPredef) {
          ImagePreview(
            if (item.predefType == PredefCeType.COLLECTION) Icons.Rounded.Favorite else Icons.Rounded.Podcasts,
            true,
            modifier = Modifier
              .weight(1f)
              .aspectRatio(1f)
              .clip(RoundedCornerShape(12.dp))
          )
        } else {
          if (item.picture.isEmpty()) {
            ImagePreview(
              Icons.Rounded.Photo,
              false,
              modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
            )
          } else {
            AsyncImage(
              model = "https://i.scdn.co/image/${item.picture}",
              contentDescription = null,
              modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
            )
          }
        }
      }

      Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
        when (item.predefType) {
          PredefCeType.COLLECTION -> {
            Text(
              text = stringResource(id = R.string.liked_songs),
              fontSize = 16.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
          }
          PredefCeType.EPISODES -> {
            Text(
              text = stringResource(id = R.string.new_episodes),
              fontSize = 16.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )
          }
          null -> {
            MarqueeText(
              text = item.name,
              fontSize = 16.sp,
              overflow = TextOverflow.Ellipsis,
              style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
            )

//            Subtext(
//              text = "Time",
//              maxLines = 1
//            )

          }
        }

        MarqueeText(
          text = when (item.predefType) {
            PredefCeType.COLLECTION -> stringResource(
              id = R.string.liked_songs_desc,
              item.predefDyn
            )
            PredefCeType.EPISODES -> stringResource(
              id = R.string.new_episodes_desc,
              item.predefDyn
            )
            null -> item.subtitle
          },
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          style = TextStyle(platformStyle = PlatformTextStyle(false)),
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
          modifier = Modifier.padding(top = 2.dp)
        )
      }
    }
  }
}


@OptIn(ExperimentalTextApi::class)
@Composable
fun YLAlbumCard(
  picUrl: String,
  picPlaceholder: String,
  title: String,
  subtitle: String?,
  modifier: Modifier
) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
    shape = RoundedCornerShape(24.dp),
    modifier = Modifier
      .height(264.dp)
      .width(164.dp)
  ) {
    Column(
      modifier = modifier.padding(14.dp)
    ) {
      Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)) {
        PreviewableAsyncImage(
          imageUrl = picUrl,
          placeholderType = picPlaceholder,
          modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
        )
      }

      Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
        MarqueeText(
          text = title,
          fontSize = 16.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )

        if (!subtitle.isNullOrEmpty()) {
          MarqueeText(
            subtitle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
          )
        }
      }
    }
  }
}


@OptIn(ExperimentalTextApi::class)
@Composable
fun YLArtistCard(
  picUrl: String,
  picPlaceholder: String,
  title: String,
  modifier: Modifier
) {
  Surface(
    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
    shape = RoundedCornerShape(24.dp),
    modifier = Modifier
      .height(264.dp)
      .width(164.dp)
  ) {
    Column(
      horizontalAlignment = CenterHorizontally,
      modifier = modifier.padding(14.dp)
    ) {
      Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)) {
        PreviewableAsyncImage(
          imageUrl = picUrl,
          placeholderType = picPlaceholder,
          modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(CircleShape)
        )
      }

      Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
        MarqueeText(
          text = title,
          fontSize = 16.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )

        Text(
          stringResource(R.string.artist),
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 1,
          style = TextStyle(platformStyle = PlatformTextStyle(false)),
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        )
      }
    }
  }
}