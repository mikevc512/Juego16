package org.juego.game.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PuzzleTest {

    @Test
    fun initialEntriesKeepGivensAndZeroOutTheRest() {
        val puzzle = sequentialEasyPuzzle()
        val entries = puzzle.initialEntries()

        assertEquals(CELL_COUNT, entries.size)
        entries.forEachIndexed { index, value ->
            if (puzzle.given[index]) {
                assertEquals(puzzle.solution[index], value)
            } else {
                assertEquals(0, value)
            }
        }
        assertEquals(Difficulty.Easy.givenCount, entries.count { it != 0 })
    }

    @Test
    fun rejectsSolutionThatIsNotAPermutationOfOneToSixteen() {
        assertFailsWith<IllegalArgumentException> {
            Puzzle(
                solution = (1..15).toList() + 15,
                given = List(CELL_COUNT) { it < Difficulty.Easy.givenCount },
                rowSums = listOf(10, 26, 42, 57),
                colSums = listOf(28, 32, 36, 39),
                difficulty = Difficulty.Easy,
            )
        }
    }

    @Test
    fun rejectsWrongGivenCountForDifficulty() {
        assertFailsWith<IllegalArgumentException> {
            Puzzle(
                solution = (1..16).toList(),
                given = List(CELL_COUNT) { it < Difficulty.Hard.givenCount },
                rowSums = listOf(10, 26, 42, 58),
                colSums = listOf(28, 32, 36, 40),
                difficulty = Difficulty.Easy,
            )
        }
    }

    @Test
    fun rejectsMismatchedRowOrColumnSums() {
        assertFailsWith<IllegalArgumentException> {
            Puzzle(
                solution = (1..16).toList(),
                given = List(CELL_COUNT) { it < Difficulty.Easy.givenCount },
                rowSums = listOf(11, 26, 42, 58),
                colSums = listOf(28, 32, 36, 40),
                difficulty = Difficulty.Easy,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            Puzzle(
                solution = (1..16).toList(),
                given = List(CELL_COUNT) { it < Difficulty.Easy.givenCount },
                rowSums = listOf(10, 26, 42, 58),
                colSums = listOf(27, 32, 36, 40),
                difficulty = Difficulty.Easy,
            )
        }
    }

    @Test
    fun rejectsWrongCollectionSizes() {
        assertFailsWith<IllegalArgumentException> {
            Puzzle(
                solution = (1..15).toList(),
                given = List(CELL_COUNT) { it < Difficulty.Easy.givenCount },
                rowSums = listOf(10, 26, 42, 58),
                colSums = listOf(28, 32, 36, 40),
                difficulty = Difficulty.Easy,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            Puzzle(
                solution = (1..16).toList(),
                given = List(15) { it < Difficulty.Easy.givenCount },
                rowSums = listOf(10, 26, 42, 58),
                colSums = listOf(28, 32, 36, 40),
                difficulty = Difficulty.Easy,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            Puzzle(
                solution = (1..16).toList(),
                given = List(CELL_COUNT) { it < Difficulty.Easy.givenCount },
                rowSums = listOf(10, 26, 42),
                colSums = listOf(28, 32, 36, 40),
                difficulty = Difficulty.Easy,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            Puzzle(
                solution = (1..16).toList(),
                given = List(CELL_COUNT) { it < Difficulty.Easy.givenCount },
                rowSums = listOf(10, 26, 42, 58),
                colSums = listOf(28, 32, 36),
                difficulty = Difficulty.Easy,
            )
        }
    }
}

internal fun sequentialEasyPuzzle(): Puzzle = Puzzle(
    solution = (1..16).toList(),
    given = List(CELL_COUNT) { it < Difficulty.Easy.givenCount },
    rowSums = listOf(10, 26, 42, 58),
    colSums = listOf(28, 32, 36, 40),
    difficulty = Difficulty.Easy,
)

class AnalyzeBoardTest {

    private val rowSums = listOf(10, 26, 42, 58)
    private val colSums = listOf(28, 32, 36, 40)
    private val solution = (1..16).toList()

    @Test
    fun emptyBoardIsIncompleteAndUnsolved() {
        val analysis = analyzeBoard(List(CELL_COUNT) { 0 }, rowSums, colSums)

        assertTrue(analysis.duplicateIndices.isEmpty())
        assertTrue(analysis.rowStates.all { it == LineState.Incomplete })
        assertTrue(analysis.colStates.all { it == LineState.Incomplete })
        assertTrue(analysis.usedNumbers.isEmpty())
        assertFalse(analysis.isFilled)
        assertFalse(analysis.isSolved)
    }

    @Test
    fun completeCorrectPermutationIsSolved() {
        val analysis = analyzeBoard(solution, rowSums, colSums)

        assertTrue(analysis.duplicateIndices.isEmpty())
        assertTrue(analysis.rowStates.all { it == LineState.Correct })
        assertTrue(analysis.colStates.all { it == LineState.Correct })
        assertEquals((1..CELL_COUNT).toSet(), analysis.usedNumbers)
        assertTrue(analysis.isFilled)
        assertTrue(analysis.isSolved)
    }

    @Test
    fun duplicateNumbersAreFlaggedAndPreventSolve() {
        val entries = solution.toMutableList().also {
            it[15] = 1
        }
        val analysis = analyzeBoard(entries, rowSums, colSums)

        assertEquals(setOf(0, 15), analysis.duplicateIndices)
        assertTrue(analysis.isFilled)
        assertFalse(analysis.isSolved)
        assertEquals(LineState.Correct, analysis.rowStates[0])
        assertEquals(LineState.Incorrect, analysis.rowStates[3])
    }

    @Test
    fun filledBoardWithWrongSumsIsIncorrectNotSolved() {
        val entries = listOf(
            1, 2, 4, 3,
            5, 6, 7, 8,
            9, 10, 11, 12,
            13, 14, 15, 16,
        )
        val analysis = analyzeBoard(entries, rowSums, colSums)

        assertTrue(analysis.isFilled)
        assertFalse(analysis.isSolved)
        assertEquals(LineState.Correct, analysis.rowStates[0])
        assertEquals(LineState.Correct, analysis.rowStates[1])
        assertEquals(LineState.Incorrect, analysis.colStates[2])
        assertEquals(LineState.Incorrect, analysis.colStates[3])
    }

    @Test
    fun partialRowStaysIncompleteEvenWhenPartialSumMatches() {
        val entries = List(CELL_COUNT) { 0 }.toMutableList().also {
            it[0] = 1
            it[1] = 2
            it[2] = 3
        }
        val analysis = analyzeBoard(entries, rowSums, colSums)

        assertEquals(LineState.Incomplete, analysis.rowStates[0])
        assertEquals(setOf(1, 2, 3), analysis.usedNumbers)
        assertFalse(analysis.isFilled)
        assertFalse(analysis.isSolved)
    }

    @Test
    fun zerosAreIgnoredForDuplicatesAndUsedNumbers() {
        val entries = listOf(1, 0, 1, 0) + List(12) { 0 }
        val analysis = analyzeBoard(entries, rowSums, colSums)

        assertEquals(setOf(0, 2), analysis.duplicateIndices)
        assertEquals(setOf(1), analysis.usedNumbers)
        assertEquals(LineState.Incomplete, analysis.rowStates[0])
    }

    @Test
    fun rejectsWrongEntryCount() {
        assertFailsWith<IllegalArgumentException> {
            analyzeBoard(listOf(1, 2, 3), rowSums, colSums)
        }
    }
}
