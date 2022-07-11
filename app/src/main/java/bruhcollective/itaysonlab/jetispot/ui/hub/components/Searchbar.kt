package bruhcollective.itaysonlab.jetispot.ui.hub.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Searchbar() {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val textToCompare by remember { mutableStateOf(TextFieldValue("")) }
    val cancelInvisible = text == textToCompare
    val textFieldWidth by animateFloatAsState(
        targetValue = when (cancelInvisible) {
            true -> 1f
            else -> 0.74f
        }
    )

    Card(
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(8.dp)
            .systemBarsPadding()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = text,
                onValueChange = { newText -> text = newText },
                modifier = Modifier.fillMaxWidth(textFieldWidth),
                placeholder = { Text("Search") },
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Gray,
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Row {
                Button(
                    onClick = { text = textToCompare},
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = "Cancel",
                        modifier = Modifier.height(20.dp)
                    )
                }
            }
        }
    }
}