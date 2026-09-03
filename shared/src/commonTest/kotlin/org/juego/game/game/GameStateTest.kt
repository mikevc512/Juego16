package org.juego.game.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameStateTest {

    @Test
    fun newGameStartsFromPuzzleGivens() {
        val state = GameState.newGame(Difficulty.Easy, PuzzleGenerator(Random(7)))

        assertEquals(state.puzzle.initialEntries(), state.entries)
        assertNull(state.selectedIndex)
        assertTrue(state.history.isEmpty())
        assertFalse(state.isSolved)
        assertFalse(state.showingErrors)
        assertEquals(Difficulty.Easy, state.puzzle.difficulty)
        assertEquals(Difficulty.Easy.givenCount, state.puzzle.given.count { it })
    }

    @Test
    fun selectRejectsOutOfRangeIndex() {
        val state = sampleGame()
        assertFailsWith<IllegalArgumentException> { state.select(-1) }
        assertFailsWith<IllegalArgumentException> { state.select(CELL_COUNT) }
    }

    @Test
    fun selectClearsErrorHighlight() {
        val state = sampleGame().check().select(6)

        assertEquals(6, state.selectedIndex)
        assertFalse(state.showingErrors)
    }

    @Test
    fun enterWithoutSelectionIsNoOp() {
        val state = sampleGame()
        assertEquals(state, state.enter(7))
    }

    @Test
    fun enterDoesNotChangeGivenCells() {
        val state = sampleGame().select(0)
        assertTrue(state.puzzle.given[0])
        assertEquals(state.entries, state.enter(16).entries)
        assertTrue(state.enter(16).history.isEmpty())
    }

    @Test
    fun enterWritesNumberAndRecordsHistory() {
        val started = sampleGame().select(6)
        val entered = started.enter(7)

        assertEquals(7, entered.entries[6])
        assertEquals(listOf(started.entries), entered.history)
        assertFalse(entered.showingErrors)
        assertTrue(entered.canUndo())
        assertTrue(entered.canErase())
    }

    @Test
    fun enteringTheSameNumberTogglesErase() {
        val entered = sampleGame().select(6).enter(7)
        val erased = entered.enter(7)

        assertEquals(0, erased.entries[6])
        assertEquals(2, erased.history.size)
        assertEquals(entered.entries, erased.history.last())
    }

    @Test
    fun eraseWithoutSelectionOrOnEmptyOrGivenIsNoOp() {
        val empty = sampleGame()
        assertEquals(empty, empty.erase())

        val givenSelected = empty.select(0)
        assertEquals(givenSelected, givenSelected.erase())

        val emptyCell = empty.select(6)
        assertEquals(emptyCell, emptyCell.erase())
        assertFalse(emptyCell.canErase())
    }

    @Test
    fun eraseClearsAPlayableValue() {
        val entered = sampleGame().select(6).enter(7)
        val erased = entered.erase()

        assertEquals(0, erased.entries[6])
        assertFalse(erased.canErase())
    }

    @Test
    fun undoRestoresPreviousEntriesAndClearsSolvedFlag() {
        val started = sampleGame()
        val playable = started.puzzle.given.indexOfFirst { !it }
        val solved = fillToSolution(started)
        assertTrue(solved.isSolved)

        val undone = solved.undo()
        assertFalse(undone.isSolved)
        assertFalse(undone.showingErrors)
        assertEquals(solved.history.dropLast(1), undone.history)
        assertEquals(solved.history.last()[playable], undone.entries[playable])
    }

    @Test
    fun undoOnEmptyHistoryIsNoOp() {
        val state = sampleGame()
        assertFalse(state.canUndo())
        assertEquals(state, state.undo())
    }

    @Test
    fun resetRestoresGivensAndClearsSelectionHistoryAndFlags() {
        val started = sampleGame().select(6).enter(7).check()
        val reset = started.reset()

        assertEquals(started.puzzle.initialEntries(), reset.entries)
        assertNull(reset.selectedIndex)
        assertTrue(reset.history.isEmpty())
        assertFalse(reset.isSolved)
        assertFalse(reset.showingErrors)
    }

    @Test
    fun checkTurnsOnErrorHighlighting() {
        assertTrue(sampleGame().check().showingErrors)
    }

    @Test
    fun fillingTheSolutionMarksSolvedAndFilled() {
        val solved = fillToSolution(sampleGame())

        assertTrue(solved.isSolved)
        assertTrue(solved.isFilled)
        assertTrue(solved.analysis.isSolved)
        assertEquals(solved.puzzle.solution, solved.entries)
    }

    @Test
    fun enterRejectsNumbersOutsideOneToSixteen() {
        val state = sampleGame().select(6)
        assertFailsWith<IllegalArgumentException> { state.enter(0) }
        assertFailsWith<IllegalArgumentException> { state.enter(17) }
    }

    @Test
    fun analysisReflectsCurrentEntries() {
        val started = sampleGame()
        assertEquals(
            analyzeBoard(started.entries, started.puzzle.rowSums, started.puzzle.colSums),
            started.analysis,
        )
    }
}

private fun sampleGame(): GameState {
    val puzzle = sequentialEasyPuzzle()
    return GameState(puzzle = puzzle, entries = puzzle.initialEntries())
}

private fun fillToSolution(state: GameState): GameState {
    var current = state
    state.puzzle.solution.forEachIndexed { index, value ->
        if (!state.puzzle.given[index]) {
            current = current.select(index).enter(value)
        }
    }
    return current
}
