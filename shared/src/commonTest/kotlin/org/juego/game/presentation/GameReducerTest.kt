package org.juego.game.presentation

import org.juego.game.game.Difficulty
import org.juego.game.game.GameState
import org.juego.game.game.PuzzleGenerator
import org.juego.game.presentation.GameIntent
import org.juego.game.presentation.GameUiState
import org.juego.game.presentation.reduce
import org.juego.game.presentation.reduceWithTimer
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameReducerTest {

    private val generator = PuzzleGenerator(Random(1))

    @Test
    fun pauseCommitsElapsedAndStopsTheClock() {
        val started = GameUiState(
            game = GameState.newGame(Difficulty.Easy, generator),
            isGameScreenVisible = true,
            elapsedSeconds = 10,
            runningSinceEpochMs = 1_000L,
        )
        val paused = reduceWithTimer(started, GameIntent.Pause, generator, nowEpochMs = 4_500L)

        assertTrue(paused.isPaused)
        assertEquals(13, paused.elapsedSeconds)
        assertEquals(null, paused.runningSinceEpochMs)
        assertEquals(13, paused.liveElapsedSeconds(9_000L))
    }

    @Test
    fun clockAdvancesFromTheAnchorOnlyWhilePlayingOnScreen() {
        val started = GameUiState(
            game = GameState.newGame(Difficulty.Easy, generator),
            isGameScreenVisible = true,
        )
        val running = reduceWithTimer(started, GameIntent.AppForegrounded, generator, nowEpochMs = 5_000L)
        val backgrounded = reduceWithTimer(running, GameIntent.AppBackgrounded, generator, nowEpochMs = 8_000L)

        assertEquals(5_000L, running.runningSinceEpochMs)
        assertEquals(0, running.elapsedSeconds)
        assertEquals(3, running.liveElapsedSeconds(8_000L))
        assertEquals(3, backgrounded.elapsedSeconds)
        assertEquals(null, backgrounded.runningSinceEpochMs)
        assertEquals(3, backgrounded.liveElapsedSeconds(12_000L))
        assertFalse(backgrounded.isForeground)
    }

    @Test
    fun boardEditKeepsTheRunningClockAnchor() {
        val running = GameUiState(
            game = GameState.newGame(Difficulty.Easy, generator),
            isGameScreenVisible = true,
            elapsedSeconds = 4,
            runningSinceEpochMs = 10_000L,
        )
        val playableIndex = running.game.puzzle.given.indexOfFirst { !it }
        val next = reduceWithTimer(running, GameIntent.SelectCell(playableIndex), generator, nowEpochMs = 15_000L)

        assertEquals(4, next.elapsedSeconds)
        assertEquals(10_000L, next.runningSinceEpochMs)
        assertEquals(9, next.liveElapsedSeconds(15_000L))
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
