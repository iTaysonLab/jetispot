package bruhcollective.itaysonlab.jetispot.ui.dac.components_plans

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import com.spotify.planoverview.v1.FallbackPlanComponent

@Composable
fun FallbackPlanComponentBinder(
    item: FallbackPlanComponent
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.compositeSurfaceElevation(
                3.dp
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .padding(top = 12.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MediumText(modifier = Modifier,
                text = item.name)
            Divider()
            MediumText(modifier = Modifier,
                text = item.description)
            Text("Seems like you don't have an Spotify Account")
        }
    }
}