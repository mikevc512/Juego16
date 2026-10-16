package org.juego.game.presentation

import org.juego.game.game.GameState
import org.juego.game.game.PuzzleGenerator

fun reduce(
    state: GameUiState,
    intent: GameIntent,
    generator: PuzzleGenerator,
): GameUiState = when (intent) {
    is GameIntent.SelectCell -> state.ifPlayable {
        copy(game = game.select(intent.index))
    }
    is GameIntent.EnterNumber -> state.ifPlayable {
        copy(game = game.enter(intent.number))
    }
    GameIntent.Erase -> state.ifPlayable { copy(game = game.erase()) }
    GameIntent.Undo -> state.ifPlayable { copy(game = game.undo()) }
    GameIntent.Reset -> state.ifPlayable {
        copy(
            game = game.reset(),
        )
    }
    GameIntent.Check -> state.ifPlayable {
        copy(
            game = game.check(),
        )
    }
    is GameIntent.NewGame -> GameUiState(
        game = GameState.newGame(intent.difficulty, generator),
        isForeground = state.isForeground,
        hasShownSuccessDialog = false,
        isGameScreenVisible = state.isGameScreenVisible,
        showSelectDifficulty = false
    )
    is GameIntent.EndGame -> state.copy(showSelectDifficulty = true)
    GameIntent.Pause -> state.copy(isPaused = true)
    GameIntent.ShowGameSelector -> state.copy(showSelectDifficulty = true)
    GameIntent.Resume -> state.copy(isPaused = false)
    GameIntent.DismissResultDialog -> {
        state.copy(game = state.game.copy(showingErrors = false))
    }
    GameIntent.DismissSuccessDialog -> {
        state.copy(hasShownSuccessDialog = true)
    }
    GameIntent.AppBackgrounded -> state.copy(isForeground = false)
    GameIntent.AppForegrounded -> state.copy(isForeground = true)
    is GameIntent.SetGameScreenVisible -> state.copy(isGameScreenVisible = intent.visible)
}

fun reduceWithTimer(
    state: GameUiState,
    intent: GameIntent,
    generator: PuzzleGenerator,
    nowEpochMs: Long,
): GameUiState {
    val reduced = reduce(state, intent, generator)
    val timerStopped = state.isTimerRunning && !reduced.isTimerRunning
    val timed = if (timerStopped) {
        reduced.copy(
            elapsedSeconds = state.liveElapsedSeconds(nowEpochMs),
            runningSinceEpochMs = null,
        )
    } else {
        reduced
    }
    return timed.withTimerAnchor(nowEpochMs)
}

private inline fun GameUiState.ifPlayable(transform: GameUiState.() -> GameUiState): GameUiState =
    if (isPaused || isRestoring) this else transform()