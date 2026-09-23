package org.juego.game.ui.gameboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import org.juego.game.game.GameState
import org.juego.game.presentation.GameIntent
import org.juego.game.presentation.GameUiState
import org.juego.game.ui.DifficultySelector

@Composable
fun GameScreenLandscape(
    uiState: GameUiState,
    state: GameState,
    cellSize: Dp,
    onIntent: (GameIntent) -> Unit,
    showDialog: () -> Unit
) {
    if (uiState.showSelectDifficulty) {
        DifficultySelector(
            selected = state.puzzle.difficulty,
            onSelect = { onIntent(GameIntent.NewGame(it)) }
        )
    } else {
        ActiveScreenLandscape(
            uiState = uiState,
            state = state,
            onIntent = onIntent,
            cellSize = cellSize,
            showDialog = showDialog
        )
    }
}