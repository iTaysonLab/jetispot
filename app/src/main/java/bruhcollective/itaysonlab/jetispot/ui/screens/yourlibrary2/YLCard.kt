package bruhcollective.itaysonlab.jetispot.ui.screens.yourlibrary2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Podcasts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetispot.R
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.*
import bruhcollective.itaysonlab.jetispot.core.collection.db.model2.rootlist.CollectionRootlistItem
import bruhcollective.itaysonlab.jetispot.ui.shared.ImagePreview
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import bruhcollective.itaysonlab.jetispot.ui.shared.PreviewableAsyncImage
import bruhcollective.itaysonlab.jetispot.ui.shared.Subtext
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





@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun YLPinnedCard(
    item: CollectionPinnedItem,
    modifier: Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = modifier
        ) {
            Surface(Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                val isPredef = item.predefType != null
                if (isPredef) {
                    ImagePreview(
                        if (item.predefType == PredefCeType.COLLECTION) Icons.Rounded.Favorite else Icons.Rounded.Podcasts,
                        true,
                        modifier = Modifier
                            .size(160.dp)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    if (item.picture.isEmpty()) {
                        ImagePreview(
                            Icons.Rounded.Photo,
                            false,
                            modifier = Modifier
                                .size(160.dp)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        AsyncImage(
                            model = "https://i.scdn.co/image/${item.picture}",
                            contentDescription = null,
                            modifier = Modifier
                                .size(160.dp)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            Column(
                Modifier
                    .height(64.dp)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                when (item.predefType) {
                    PredefCeType.COLLECTION -> {
                        MediumText(
                            text = stringResource(id = R.string.liked_songs)
                        )
                    }
                    PredefCeType.EPISODES -> {
                        MediumText(
                            text = stringResource(id = R.string.new_episodes)
                        )
                    }
                    null -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MediumText(
                                text = item.name
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Subtext(
                                text = "Time",
                                maxLines = 1
                            )
                        }
                    }
                }

                Subtext(
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
                    modifier = Modifier.padding(top = 2.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun YLAlbumCard(
    picUrl: String,
    picPlaceholder: String,
    title: String,
    subtitle: String?,
    modifier: Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = modifier
        ) {
            Surface(Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                PreviewableAsyncImage(
                    imageUrl = picUrl,
                    placeholderType = picPlaceholder,
                    modifier = Modifier
                        .size(160.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Column(
                Modifier
                    .height(64.dp)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                MediumText(
                    title,
                    textAlign = TextAlign.Start
                )

                if (!subtitle.isNullOrEmpty()) {
                    Subtext(
                        subtitle,
                        modifier = Modifier
                            .padding(top = 4.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun YLArtistCard(
    picUrl: String,
    picPlaceholder: String,
    title: String,
    modifier: Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = modifier
        ) {
            Surface(Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                PreviewableAsyncImage(
                    imageUrl = picUrl,
                    placeholderType = picPlaceholder,
                    modifier = Modifier
                        .size(160.dp)
                        .padding(8.dp)
                        .clip(CircleShape)
                )
            }

            Column(
                Modifier
                    .height(64.dp)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                MediumText(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Subtext(
                    "Artist",
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}