package org.juego.game.presentation

import org.juego.game.game.Difficulty
import org.juego.game.game.GameState
import org.juego.game.game.PuzzleGenerator
import org.juego.game.presentation.GameIntent
import org.juego.game.presentation.GameUiState
import org.juego.game.presentation.reduce
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameReducerTest {

    private val generator = PuzzleGenerator(Random(1))

    @Test
    fun tickDoesNotAdvanceWhenPaused() {
        val started = GameUiState(
            game = GameState.newGame(Difficulty.Easy, generator),
            isGameScreenVisible = true,
        )
        val paused = reduce(started, GameIntent.Pause, generator)
        val ticked = reduce(paused, GameIntent.Tick, generator)

        assertTrue(paused.isPaused)
        assertEquals(0, ticked.elapsedSeconds)
    }

    @Test
    fun tickAdvancesOnlyWhilePlayingOnScreen() {
        val started = GameUiState(
            game = GameState.newGame(Difficulty.Easy, generator),
            isGameScreenVisible = true,
        )
        val ticked = reduce(started, GameIntent.Tick, generator)
        val backgrounded = reduce(ticked, GameIntent.AppBackgrounded, generator)
        val ignored = reduce(backgrounded, GameIntent.Tick, generator)

        assertEquals(1, ticked.elapsedSeconds)
        assertEquals(1, ignored.elapsedSeconds)
        assertFalse(backgrounded.isForeground)
    }

    @Test
    fun pauseBlocksBoardEditsUntilResume() {
        val started = GameUiState(
            game = GameState.newGame(Difficulty.Easy, generator),
            isGameScreenVisible = true,
        )
        val playableIndex = started.game.puzzle.given.indexOfFirst { !it }
        val unusedNumber = (1..16).first { it !in started.game.analysis.usedNumbers }
        val selected = reduce(started, GameIntent.SelectCell(playableIndex), generator)
        val paused = reduce(selected, GameIntent.Pause, generator)
        val blocked = reduce(paused, GameIntent.EnterNumber(unusedNumber), generator)
        val resumed = reduce(blocked, GameIntent.Resume, generator)
        val entered = reduce(resumed, GameIntent.EnterNumber(unusedNumber), generator)

        assertEquals(selected.game.entries, blocked.game.entries)
        assertFalse(resumed.isPaused)
        assertEquals(unusedNumber, entered.game.entries[playableIndex])
    }
}
