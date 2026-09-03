package org.juego.game.ui.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.back_to_board
import kotlinproject.shared.generated.resources.puzzle_unsolved_message
import kotlinproject.shared.generated.resources.puzzle_unsolved_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun IncorrectPuzzleDialog(
    modifier: Modifier,
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    stringResource(
                        Res.string.puzzle_unsolved_title
                    )
                )
            },
            text = {
                Text(stringResource(Res.string.puzzle_unsolved_message))
            },
            confirmButton = { },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.back_to_board))
                }
            },
        )
    }
}