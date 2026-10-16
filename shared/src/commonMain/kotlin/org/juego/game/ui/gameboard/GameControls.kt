package org.juego.game.ui.gameboard

import androidx.compose.runtime.Composable
import org.juego.game.game.Difficulty
import org.juego.game.presentation.GameIntent

@Composable
fun GameControls(
    usedNumbers: Set<Int>,
    isSolved: Boolean,
    difficulty: Difficulty,
    isPaused: Boolean,
    canUndo: Boolean,
    canErase: Boolean,
    allCellsOccupied: Boolean,
    onIntent: (GameIntent) -> Unit,
    showDialog: () -> Unit
) {
    NumberPad(
        usedNumbers = usedNumbers,
        enabled = !isSolved && !isPaused,
        onNumber = { onIntent(GameIntent.EnterNumber(it)) },
    )
    ActionRow(
        difficulty = difficulty,
        canUndo = canUndo,
        canErase = canErase,
        allCellsOccupied = allCellsOccupied,
        isPaused = isPaused,
        isSolved = isSolved,
        onIntent = onIntent,
        onEnd = { showDialog() },
    )
}