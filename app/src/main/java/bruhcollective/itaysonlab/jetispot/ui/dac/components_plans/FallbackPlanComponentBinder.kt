package bruhcollective.itaysonlab.jetispot.ui.dac.components_plans

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.ext.compositeSurfaceElevation
import bruhcollective.itaysonlab.jetispot.ui.shared.MediumText
import com.spotify.planoverview.v1.FallbackPlanComponent
import bruhcollective.itaysonlab.jetispot.R

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
            MediumText(
                modifier = Modifier,
                text = item.name
            )
            Divider(modifier = Modifier.padding(top = 6.dp, bottom = 6.dp))
            MediumText(
                modifier = Modifier,
                text = item.description
            )
            Text(stringResource(id = R.string.plan_overview_fallback_plan))
        }
    }
}