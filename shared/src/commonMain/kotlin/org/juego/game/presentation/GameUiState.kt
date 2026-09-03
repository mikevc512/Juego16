package org.juego.game.presentation

import org.juego.game.game.GameState

data class GameUiState(
    val game: GameState,
    val elapsedSeconds: Int = 0,
    val isResultDialogDismissed: Boolean = false,
    val showAd: Boolean = false,
    val isPaused: Boolean = false,
    val showSelectDifficulty: Boolean = false,
    val isForeground: Boolean = true,
    val isGameScreenVisible: Boolean = false,
    val isRestoring: Boolean = false,
    val hasShownSuccessDialog: Boolean = false
) {
    val isTimerRunning: Boolean
        get() = !game.isSolved && !isPaused && isForeground && isGameScreenVisible && !showSelectDifficulty
}