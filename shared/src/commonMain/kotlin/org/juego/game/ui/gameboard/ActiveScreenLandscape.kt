package org.juego.game.ui.gameboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import org.juego.game.ui.GameHeader

@Composable
fun ActiveScreenLandscape(
    uiState: GameUiState,
    state: GameState, cellSize: Dp,
    onIntent: (GameIntent) -> Unit,
    showDialog: () -> Unit
) {

    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.verticalScroll(scrollState).padding(bottom = 42.dp),
        ) {
            GameHeader(
                modifier = Modifier.widthIn(max = 280.dp).align(alignment = Alignment.CenterHorizontally),
                elapsedSeconds = uiState.elapsedSeconds,
                difficulty = state.puzzle.difficulty,
                isPaused = uiState.isPaused
            )
            KubokBoard(
                modifier = Modifier.padding(end = 52.dp),
                state = state,
                cellSize = cellSize,
                onCellClick = { if (!uiState.isPaused) onIntent(GameIntent.SelectCell(it)) },
            )
        }

        Column(
            modifier = Modifier.widthIn(max = 360.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GameControls(
                usedNumbers = state.analysis.usedNumbers,
                isSolved = state.isSolved,
                isPaused = uiState.isPaused,
                difficulty = uiState.game.puzzle.difficulty,
                canUndo = state.canUndo(),
                canErase = state.canErase(),
                allCellsOccupied = state.isFilled,
                onIntent = onIntent,
                showDialog = showDialog
            )
        }
    }
}