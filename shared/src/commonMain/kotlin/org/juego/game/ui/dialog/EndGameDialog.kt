package org.juego.game.ui.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.back_to_board
import kotlinproject.shared.generated.resources.end_game_message
import kotlinproject.shared.generated.resources.end_game_title
import kotlinproject.shared.generated.resources.new_puzzle
import org.jetbrains.compose.resources.stringResource

@Composable
fun EndGameDialog(
    modifier: Modifier,
    visible: Boolean,
    onEndGame: () -> Unit,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        exit = fadeOut(animationSpec = tween(durationMillis = 0))
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(Res.string.end_game_title)) },
            text = {
                Text(stringResource(Res.string.end_game_message))
            },
            confirmButton = {
                Button(onClick = onEndGame) {
                    Text(stringResource(Res.string.new_puzzle))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.back_to_board))
                }
            }
        )
    }
}