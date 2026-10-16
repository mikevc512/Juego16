package org.juego.game.presentation

import org.juego.game.game.GameState

data class GameUiState(
    val game: GameState,
    val elapsedSeconds: Int = 0,
    val runningSinceEpochMs: Long? = null,
    val isResultDialogDismissed: Boolean = false,
    val isPaused: Boolean = false,
    val showSelectDifficulty: Boolean = false,
    val isForeground: Boolean = true,
    val isGameScreenVisible: Boolean = false,
    val isRestoring: Boolean = false,
    val hasShownSuccessDialog: Boolean = false
) {
    val isTimerRunning: Boolean
        get() = !game.isSolved && !isPaused && isForeground && isGameScreenVisible && !showSelectDifficulty

    fun liveElapsedSeconds(nowEpochMs: Long): Int = elapsedSecondsAt(
        baseSeconds = elapsedSeconds,
        runningSinceEpochMs = if (isTimerRunning) runningSinceEpochMs else null,
        nowEpochMs = nowEpochMs,
    )

    fun withTimerAnchor(nowEpochMs: Long): GameUiState = when {
        isTimerRunning && runningSinceEpochMs == null -> copy(runningSinceEpochMs = nowEpochMs)
        !isTimerRunning && runningSinceEpochMs != null -> copy(runningSinceEpochMs = null)
        else -> this
    }
}

fun elapsedSecondsAt(
    baseSeconds: Int,
    runningSinceEpochMs: Long?,
    nowEpochMs: Long,
): Int {
    val since = runningSinceEpochMs ?: return baseSeconds
    val deltaSeconds = ((nowEpochMs - since) / 1_000L).toInt()
    return baseSeconds + deltaSeconds.coerceAtLeast(0)
}