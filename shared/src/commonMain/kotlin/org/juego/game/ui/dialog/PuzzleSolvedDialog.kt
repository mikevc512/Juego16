package org.juego.game.ui.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.back_to_board
import kotlinproject.shared.generated.resources.new_puzzle
import kotlinproject.shared.generated.resources.puzzle_solved_message
import kotlinproject.shared.generated.resources.puzzle_solved_title
import org.jetbrains.compose.resources.stringResource
import org.juego.game.ui.gameboard.ConfettiAnimation

@Composable
fun PuzzleSolvedDialog(
    modifier: Modifier,
    visible: Boolean,
    elapsedSeconds: Int,
    onNewGame: () -> Unit,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
    ) {

        ConfettiAnimation()

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    stringResource(Res.string.puzzle_solved_title)
                )
            },
            text = {
                Text(
                    stringResource(
                        Res.string.puzzle_solved_message,
                        formatTime(elapsedSeconds)
                    )
                )
            },
            confirmButton = {
                Button(onClick = onNewGame) {
                    Text(stringResource(Res.string.new_puzzle))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.back_to_board))
                }
            },
        )
    }
}

fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val secondText = if (seconds < 10) "0$seconds" else seconds.toString()
    return "$minutes:$secondText"
}