package org.juego.game.local

import org.juego.game.local.model.CompletedGameEntity
import org.juego.game.game.Difficulty
import org.juego.game.game.GameState
import org.juego.game.game.Puzzle
import org.juego.game.presentation.GameUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GamePersistenceMapperTest {

    @Test
    fun savedGameRoundTripPreservesBoardAndTimer() {
        val state = GameUiState(
            game = sampleGame().select(6).enter(7).select(8),
            elapsedSeconds = 93,
            isPaused = true
        )

        val entity = GamePersistenceMapper.toSavedGameEntity(state, savedAtEpochMs = 1_000L)
        val restored = GamePersistenceMapper.toUiState(entity)

        assertEquals(state.game.puzzle, restored.game.puzzle)
        assertEquals(state.game.entries, restored.game.entries)
        assertEquals(state.game.history, restored.game.history)
        assertEquals(state.game.selectedIndex, restored.game.selectedIndex)
        assertEquals(93, restored.elapsedSeconds)
        assertTrue(restored.isPaused)
        assertFalse(restored.isRestoring)
    }

    @Test
    fun statsAggregateCompletedGamesByDifficulty() {
        val games = listOf(
            CompletedGameEntity(difficulty = "Easy", elapsedSeconds = 40, completedAtEpochMs = 1),
            CompletedGameEntity(difficulty = "Easy", elapsedSeconds = 20, completedAtEpochMs = 2),
            CompletedGameEntity(difficulty = "Hard", elapsedSeconds = 120, completedAtEpochMs = 3),
        )

        val stats = GamePersistenceMapper.toUserStats(games)

        assertEquals(3, stats.totalGamesFinished)
        assertEquals(2, stats.forDifficulty(Difficulty.Easy).gamesCompleted)
        assertEquals(20, stats.forDifficulty(Difficulty.Easy).bestElapsedSeconds)
        assertEquals(30, stats.forDifficulty(Difficulty.Easy).averageElapsedSeconds)
        assertEquals(1, stats.forDifficulty(Difficulty.Hard).gamesCompleted)
        assertEquals(0, stats.forDifficulty(Difficulty.Medium).gamesCompleted)
        assertEquals(listOf(40, 20), stats.forDifficulty(Difficulty.Easy).completionTimesSeconds)
    }

    private fun sampleGame(): GameState {
        val puzzle = Puzzle(
            solution = (1..16).toList(),
            given = List(16) { it < Difficulty.Easy.givenCount },
            rowSums = listOf(10, 26, 42, 58),
            colSums = listOf(28, 32, 36, 40),
            difficulty = Difficulty.Easy,
        )
        return GameState(puzzle = puzzle, entries = puzzle.initialEntries())
    }
}
