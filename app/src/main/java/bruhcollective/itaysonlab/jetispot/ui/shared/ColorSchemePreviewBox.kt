package bruhcollective.itaysonlab.jetispot.ui.shared

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bruhcollective.itaysonlab.jetispot.ui.monet.color
import bruhcollective.itaysonlab.jetispot.ui.monet.google.scheme.Scheme

@Composable
fun colorSchemePreviewBoxV1(
    primary: Color = MaterialTheme.colorScheme.primary,
    tertiary: Color = MaterialTheme.colorScheme.tertiary,
    secondary: Color = MaterialTheme.colorScheme.secondary,
    SchemeColor: String = "",
    dark: Boolean = true,
    onClick: () -> Unit
){
    var Primary: Color = Color.Black
    var Tertiary: Color = Color.Black
    var Secondary: Color = Color.Black

    if (SchemeColor.isNotEmpty()){
        val scheme = if (dark) {Scheme.dark(android.graphics.Color.parseColor(SchemeColor))} else {Scheme.light(android.graphics.Color.parseColor(SchemeColor))}
        Primary = scheme.primary.color()
        Tertiary = scheme.primaryContainer.color().copy(alpha = 0.5f)
        Secondary = scheme.secondary.color()
    } else {
        Primary = primary
        Tertiary = tertiary
        Secondary = secondary
        }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .size(64.dp)
            .clickable(
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(54.dp)
        ){
            Column(modifier = Modifier.fillMaxHeight()){
                Box(
                    modifier = Modifier
                        .background(Primary)
                        .size(width = 54.dp, height = 27.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .background(Secondary)
                            .size(width = 27.dp, height = 27.dp)
                    )
                    Box(
                        modifier = Modifier
                            .background(Tertiary)
                            .size(width = 27.dp, height = 27.dp)
                    )
                }
            }
        }
    }
}





@Composable
fun colorSchemePreviewBoxV2(
    primary: Color = MaterialTheme.colorScheme.primary,
    tertiary: Color = MaterialTheme.colorScheme.tertiary,
    secondary: Color = MaterialTheme.colorScheme.secondary,
    SchemeColor: String = "",
    dark: Boolean = true
){
    var Primary: Color = Color.Black
    var Tertiary: Color = Color.Black
    var Secondary: Color = Color.Black

    if (SchemeColor.isNotEmpty()){
        val scheme = if (dark) {Scheme.dark(android.graphics.Color.parseColor(SchemeColor))} else {Scheme.light(android.graphics.Color.parseColor(SchemeColor))}
        Primary = scheme.primary.color()
        Tertiary = scheme.primaryContainer.color().copy(alpha = 0.5f)
        Secondary = scheme.secondary.color()
    } else {
        Primary = primary
        Tertiary = tertiary
        Secondary = secondary
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .size(64.dp)
            .padding(2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .size(54.dp)
        ){
            Column(modifier = Modifier.fillMaxHeight()) {
                Box(
                    modifier = Modifier
                        .background(Tertiary)
                        .size(width = 54.dp, height = 27.dp)
                )
                Box(
                    modifier = Modifier
                        .background(Secondary)
                        .size(width = 54.dp, height = 27.dp)
                )
            }
            Canvas(modifier = Modifier.size(27.dp)){
                val canvasWidth = size.width
                val canvasHeight = size.height
                drawCircle(
                    color = Primary,
                    center = Offset(x = canvasWidth, y = canvasHeight),
                    radius = size.minDimension / 2
                )
            }
        }
    }
}