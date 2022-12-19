package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.core.objs.hub.HubItem
import bruhcollective.itaysonlab.jetispot.ui.navigation.LocalNavigationController
import com.spotify.podcastcreatorinteractivity.v1.PodcastRating
import com.spotify.podcastextensions.proto.PodcastTopic
import com.spotify.podcastextensions.proto.PodcastTopics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastTopicsStrip(
    item: HubItem
) {
    val navController = LocalNavigationController.current
    val topics = remember { item.custom!!["topics"] as PodcastTopics }
    val rating = remember { item.custom!!["ratings"] as PodcastRating }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            ElevatedSuggestionChip(onClick = {
                // TODO
            }, icon = {
                Icon(imageVector = Icons.Rounded.Star, contentDescription = null)
            }, label = {
                Text(
                    text = "${
                        String.format(
                            "%.2f",
                            rating.averageRating.average
                        )
                    } (${rating.averageRating.totalRatings})"
                )
            })
        }

        items(topics.topicsList) { topic ->
            PodcastTopic(topic = topic, onClick = navController::navigate)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PodcastTopic(
    topic: PodcastTopic,
    onClick: (String) -> Unit
) {
    ElevatedSuggestionChip(onClick = {
        onClick(topic.uri)
    }, label = {
        Text(text = topic.title)
    })
}