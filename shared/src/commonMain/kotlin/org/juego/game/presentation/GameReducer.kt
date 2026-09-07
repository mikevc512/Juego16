package org.juego.game.presentation

import org.juego.game.game.GameState
import org.juego.game.game.PuzzleGenerator
import org.juego.game.presentation.GameIntent.PersistGame

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
    GameIntent.ShowAd -> state.copy(showAd = true)
    GameIntent.DismissAd -> state.copy(showAd = false)
    GameIntent.Tick -> if (state.isTimerRunning) {
        state.copy(elapsedSeconds = state.elapsedSeconds + 1)
    } else {
        state
    }
    GameIntent.Pause -> state.copy(isPaused = true)
    GameIntent.ShowGameSelector -> state.copy(showSelectDifficulty = true)
    GameIntent.Resume -> state.copy(isPaused = false)
    GameIntent.DismissResultDialog -> {
        state.copy(game = state.game.copy(showingErrors = false))
    }
    GameIntent.DismissSuccessDialog -> {
        state.copy(hasShownSuccessDialog = true)
    }
    PersistGame -> state
    GameIntent.AppBackgrounded -> state.copy(isForeground = false)
    GameIntent.AppForegrounded -> state.copy(isForeground = true)
    is GameIntent.SetGameScreenVisible -> state.copy(isGameScreenVisible = intent.visible)
}

private inline fun GameUiState.ifPlayable(transform: GameUiState.() -> GameUiState): GameUiState =
    if (isPaused || isRestoring) this else transform()