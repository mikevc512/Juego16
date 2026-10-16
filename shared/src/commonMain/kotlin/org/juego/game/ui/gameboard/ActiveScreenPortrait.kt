package org.juego.game.ui.gameboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.juego.game.game.GameState
import org.juego.game.presentation.GameIntent
import org.juego.game.presentation.GameUiState

@Composable
fun ActiveScreenPortrait(
    uiState: GameUiState,
    state: GameState, cellSize: Dp,
    onIntent: (GameIntent) -> Unit,
    showDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GameHeader(
            elapsedSeconds = uiState.elapsedSeconds,
            runningSinceEpochMs = uiState.runningSinceEpochMs,
            difficulty = state.puzzle.difficulty,
            isPaused = uiState.isPaused,
        )
        JuegoBoard(
            modifier = Modifier.padding(end = 32.dp, top = 24.dp),
            state = state,
            cellSize = cellSize,
            onCellClick = { if (!uiState.isPaused) onIntent(GameIntent.SelectCell(it)) },
        )
        Spacer(Modifier.height(20.dp))
        NumberPad(
            usedNumbers = state.analysis.usedNumbers,
            enabled = !state.isSolved && !uiState.isPaused,
            onNumber = { onIntent(GameIntent.EnterNumber(it)) },
            modifier = Modifier.widthIn(max = 520.dp),
        )
        Spacer(Modifier.height(16.dp))
        ActionRow(
            canUndo = state.canUndo(),
            canErase = state.canErase(),
            allCellsOccupied = state.isFilled,
            isPaused = uiState.isPaused,
            isSolved = uiState.game.isSolved,
            onEnd = { showDialog() },
            onIntent = onIntent,
            difficulty = uiState.game.puzzle.difficulty
        )
        Spacer(Modifier.height(72.dp))
    }
}