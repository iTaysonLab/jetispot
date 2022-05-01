package bruhcollective.itaysonlab.jetispot.ui.dac

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bruhcollective.itaysonlab.jetispot.ui.dac.components.BenefitListComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components.DisclaimerComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components.MultiUserMemberComponentBinder
import bruhcollective.itaysonlab.jetispot.ui.dac.components.PlanComponentBinder
import com.google.protobuf.Message
import com.spotify.allplans.v1.DisclaimerComponent
import com.spotify.allplans.v1.PlanComponent
import com.spotify.planoverview.v1.BenefitListComponent
import com.spotify.planoverview.v1.MultiUserMemberComponent

@Composable
fun DacRender (
  navController: NavController,
  item: Message
) {
  when (item) {
    is MultiUserMemberComponent -> MultiUserMemberComponentBinder(navController, item)
    is BenefitListComponent -> BenefitListComponentBinder(navController, item)
    is PlanComponent -> PlanComponentBinder(navController, item)
    is DisclaimerComponent -> DisclaimerComponentBinder(navController, item)
    else -> {
      Text("DAC [${item::class.java.simpleName}]\n\n${item}")
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}