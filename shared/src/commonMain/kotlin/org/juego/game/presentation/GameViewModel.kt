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
import org.juego.game.game.Difficulty
import org.juego.game.game.GameState
import org.juego.game.game.PuzzleGenerator
import org.juego.game.local.GameRepository
import org.juego.game.local.currentEpochMs

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
                val restoredState = restored?.copy(
                    isRestoring = false,
                    isForeground = true,
                    isGameScreenVisible = current.isGameScreenVisible,
                ) ?: current.copy(isRestoring = false)
                restoredState.withTimerAnchor(currentEpochMs())
            }
            var lastElapsedSecond = -1
            while (true) {
                delay(1_000)
                val state = uiState.value
                if (!state.isTimerRunning) {
                    lastElapsedSecond = -1
                    continue
                }
                val elapsed = state.liveElapsedSeconds(currentEpochMs())
                if (elapsed == lastElapsedSecond) continue
                lastElapsedSecond = elapsed
                when (elapsed) {
                    20 -> onIntent(GameIntent.ShowAd)
                    40 -> onIntent(GameIntent.DismissAd)
                    60 -> onIntent(GameIntent.ShowAd)
                }
                if (elapsed % 5 == 0) persistCheckpoint()
            }
        }
    }

    fun onIntent(intent: GameIntent) {
        val previous = _uiState.value
        _uiState.update { current ->
            reduceWithTimer(current, intent, generator, currentEpochMs())
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
                        elapsedSeconds = next.liveElapsedSeconds(currentEpochMs()),
                    )
                }
                if (shouldPersist(intent)) {
                    repository.saveInProgressGame(latest.withLiveElapsed())
                }
            }
        }
    }

    private fun shouldPersist(intent: GameIntent): Boolean = when (intent) {
        GameIntent.EndGame -> false
        GameIntent.ShowAd, GameIntent.DismissAd, GameIntent.AppForegrounded -> false
        is GameIntent.SetGameScreenVisible -> !intent.visible
        else -> true
    }

    private fun persistCheckpoint() {
        viewModelScope.launch {
            persistMutex.withLock {
                val latest = _uiState.value
                if (latest.isRestoring || !latest.isTimerRunning || latest.showSelectDifficulty) return@withLock
                repository.saveInProgressGame(latest.withLiveElapsed())
            }
        }
    }
}

private fun GameUiState.withLiveElapsed(): GameUiState =
    copy(elapsedSeconds = liveElapsedSeconds(currentEpochMs()))