package org.juego.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.action_check
import kotlinproject.shared.generated.resources.action_end
import kotlinproject.shared.generated.resources.action_erase
import kotlinproject.shared.generated.resources.action_pause
import kotlinproject.shared.generated.resources.action_reset
import kotlinproject.shared.generated.resources.action_resume
import kotlinproject.shared.generated.resources.action_undo
import kotlinproject.shared.generated.resources.new
import org.jetbrains.compose.resources.stringResource
import org.juego.game.game.Difficulty
import org.juego.game.presentation.GameIntent

@Composable
fun ActionRow(
    difficulty: Difficulty,
    canUndo: Boolean,
    canErase: Boolean,
    allCellsOccupied: Boolean = false,
    isPaused: Boolean = false,
    isSolved: Boolean = false,
    onEnd: () -> Unit,
    onIntent: (GameIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = {
                    if (isSolved) {
                        onIntent(GameIntent.NewGame(difficulty = difficulty))
                    } else {
                        onEnd()
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                val buttonTextId = if (isSolved) Res.string.new else Res.string.action_end
                Text(stringResource(buttonTextId))
            }
            OutlinedButton(
                onClick = { onIntent(GameIntent.Undo) },
                enabled = (canUndo && !isPaused) || !isSolved,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(stringResource(Res.string.action_undo))
            }
            OutlinedButton(
                onClick = { onIntent(GameIntent.Erase) },
                enabled = (canErase && !isPaused) || !isSolved,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(stringResource(Res.string.action_erase))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = { onIntent(GameIntent.Check) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                enabled = allCellsOccupied && !isPaused && !isSolved,
            ) {
                Text(stringResource(Res.string.action_check))
            }
            OutlinedButton(
                onClick = { onIntent(GameIntent.Reset) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                enabled = !isPaused && !isSolved,
            ) {
                Text(stringResource(Res.string.action_reset))
            }
            Button(
                onClick = { onIntent(if (isPaused) GameIntent.Resume else GameIntent.Pause) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                enabled = !isSolved,
            ) {
                Text(stringResource(if (isPaused) Res.string.action_resume else Res.string.action_pause))
            }
        }
    }
}