package org.juego.game.ui.dialog

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.how_to_play_got_it
import kotlinproject.shared.generated.resources.how_to_play_message
import kotlinproject.shared.generated.resources.how_to_play_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun HowToPlayDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.how_to_play_title)) },
        text = {
            Text(
                text = stringResource(Res.string.how_to_play_message),
                modifier = Modifier
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState()),
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(Res.string.how_to_play_got_it))
            }
        },
    )
}
