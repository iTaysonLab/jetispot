package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Podcasts
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.*
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import bruhcollective.itaysonlab.jetispot.ui.shared.ImagePreview
import bruhcollective.itaysonlab.jetispot.ui.shared.MarqueeText
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import coil.compose.AsyncImage

@Composable
fun YlRenderer(
  item: CollectionEntry,
  modifier: Modifier
) {
  when (item) {
    is CollectionPinnedItem -> YLRPinned(item, modifier)
    is CollectionRootlistItem -> YLRRootlist(item, modifier)
    is CollectionAlbum -> YLRAlbum(item, modifier)
    is CollectionArtist -> YLRArtist(item, modifier)
    else -> Text(item.toString())
  }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun YLRPinned(
  item: CollectionPinnedItem,
  modifier: Modifier
) {
  Box(
    Modifier
      .height(86.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
  ) {
    Row(modifier.padding(14.dp)) {
      val isPredef = item.predefType != null

      if (isPredef) {
        ImagePreview(
          if (item.predefType == PredefCeType.COLLECTION) Icons.Rounded.Favorite else Icons.Rounded.Podcasts,
          true,
          modifier = Modifier
            .size(58.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
        )
      } else {
        if (item.picture.isEmpty()) {
          ImagePreview(
            Icons.Rounded.Photo,
            false,
            modifier = Modifier
              .size(58.dp)
              .aspectRatio(1f)
              .clip(RoundedCornerShape(12.dp))
          )
        } else {
          AsyncImage(
            model = "https://i.scdn.co/image/${item.picture}",
            contentDescription = null,
            modifier = Modifier
              .size(58.dp)
              .aspectRatio(1f)
              .clip(RoundedCornerShape(12.dp))
          )
        }
      }

      Column(
        Modifier
          .padding(start = 16.dp)
          .align(Alignment.Top)
      ) {
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
            MediumText(
              text = stringResource(id = R.string.new_episodes)
            )
          }
          null -> {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              MarqueeText(
                text = item.name,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
              )

//              Subtext(
//                text = "Time",
//                maxLines = 1
//              )
            }
          }
        }

        Row(Modifier.padding(top = 4.dp)) {
          Icon(
            Icons.Rounded.PushPin,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
            modifier = Modifier
              .size(16.dp)
              .rotate(45f)
              .align(Alignment.CenterVertically)
          )

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
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 4.dp)
          )
        }
      }
    }
  }
}

@Composable
fun YLRRootlist(
  item: CollectionRootlistItem,
  modifier: Modifier
) {
  YLRGenericAlbumItem(
    picUrl = item.picture,
    picPlaceholder = "playlist",
    title = item.name,
    subtitle = item.ownerUsername,
    modifier = modifier
  )
}

@Composable
fun YLRAlbum(
  item: CollectionAlbum,
  modifier: Modifier
) {
  YLRGenericAlbumItem(
    picUrl = "https://i.scdn.co/image/${item.picture}",
    picPlaceholder = "album",
    title = item.name,
    subtitle = item.rawArtistsData.split("|").joinToString { it.split("=")[1] },
    modifier = modifier
  )
}

@Composable
fun YLRArtist(
  item: CollectionArtist,
  modifier: Modifier
) {
  YLRGenericArtistItem(
    picUrl = "https://i.scdn.co/image/${item.picture}",
    picPlaceholder = "artist",
    title = item.name,
    modifier = modifier
  )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun YLRGenericAlbumItem(
  picUrl: String,
  picPlaceholder: String,
  title: String,
  subtitle: String?,
  modifier: Modifier
) {
  Box(
    Modifier
      .height(86.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
  ) {
    Row(modifier.padding(14.dp)) {
      PreviewableAsyncImage(
        imageUrl = picUrl,
        placeholderType = picPlaceholder,
        modifier = Modifier
          .size(58.dp)
          .aspectRatio(1f)
          .clip(RoundedCornerShape(12.dp))
      )

      Column(
        Modifier
          .padding(start = 16.dp)
          .align(Alignment.Top)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          MarqueeText(
            text = title,
            fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
          )
          Spacer(modifier = Modifier.width(2.dp))
//          Subtext(
//            text = "Time",
//            maxLines = 1
//          )
        }

        if (!subtitle.isNullOrEmpty()) {
          MarqueeText(
            text = subtitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            style = TextStyle(platformStyle = PlatformTextStyle(false)),
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
    }
  }
}



@OptIn(ExperimentalTextApi::class)
@Composable
fun YLRGenericArtistItem(
  picUrl: String,
  picPlaceholder: String,
  title: String,
  modifier: Modifier
) {
  Box(
    Modifier
      .height(88.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
  ) {
    Row(modifier.padding(14.dp)) {
      PreviewableAsyncImage(
        imageUrl = picUrl,
        placeholderType = picPlaceholder,
        modifier = Modifier
          .size(58.dp)
          .aspectRatio(1f)
          .clip(CircleShape)
      )

      Column(
        Modifier
          .padding(start = 16.dp)
          .align(Alignment.Top)
      ) {
        MarqueeText(
          text = title,
          fontSize = 16.sp,
          style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )

        Text(
          stringResource(R.string.artist),
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
          style = TextStyle(platformStyle = PlatformTextStyle(false)),
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    }
  }
}