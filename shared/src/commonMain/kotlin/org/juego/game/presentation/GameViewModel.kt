package org.juego.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.juego.game.local.GameRepository
import org.juego.game.game.Difficulty
import org.juego.game.game.GameState
import org.juego.game.game.PuzzleGenerator

class GameViewModel(
    private val generator: PuzzleGenerator,
    private val repository: GameRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        GameUiState(
            game = GameState.newGame(Difficulty.Easy, generator),
            isRestoring = true,
        ),
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private val persistMutex = Mutex()

    init {
        viewModelScope.launch {
            val restored = repository.loadSavedGame()
            _uiState.update { current ->
                restored?.copy(
                    isRestoring = false,
                    isForeground = true,
                    isGameScreenVisible = current.isGameScreenVisible,
                ) ?: current.copy(isRestoring = false)
            }
            while (true) {
                delay(1_000)
                onIntent(GameIntent.Tick)
                val state = uiState.value
                if (!state.isTimerRunning) continue
                when (state.elapsedSeconds) {
                    20 -> onIntent(GameIntent.ShowAd)
                    40 -> onIntent(GameIntent.DismissAd)
                    60 -> onIntent(GameIntent.ShowAd)
                }
            }
        }
    }

    fun onIntent(intent: GameIntent) {
        val previous = _uiState.value
        _uiState.update { current ->
            reduce(current, intent, generator)
        }
        val next = _uiState.value
        persistIfNeeded(intent, previous, next)
    }

    private fun persistIfNeeded(
        intent: GameIntent,
        previous: GameUiState,
        next: GameUiState,
    ) {
        if (next.isRestoring) return
        viewModelScope.launch {
            persistMutex.withLock {
                val latest = _uiState.value
                if (latest.isRestoring) return@withLock
                if (intent is GameIntent.EndGame || latest.showSelectDifficulty) {
                    repository.clearSavedGame()
                    return@withLock
                }
                val justSolved = !previous.game.isSolved && next.game.isSolved
                if (justSolved) {
                    repository.recordCompletedGame(
                        difficulty = next.game.puzzle.difficulty,
                        elapsedSeconds = next.elapsedSeconds,
                    )
                }
                if (shouldPersist(intent, previous, next)) {
                    repository.saveInProgressGame(latest)
                }
            }
        }
    }

    private fun shouldPersist(
        intent: GameIntent,
        previous: GameUiState,
        next: GameUiState,
    ): Boolean = when (intent) {
        GameIntent.EndGame -> false
        GameIntent.Tick ->
            next.isTimerRunning &&
                next.elapsedSeconds != previous.elapsedSeconds &&
                next.elapsedSeconds % 5 == 0
        GameIntent.ShowAd, GameIntent.DismissAd, GameIntent.AppForegrounded -> false
        is GameIntent.SetGameScreenVisible -> !intent.visible
        else -> true
    }
}