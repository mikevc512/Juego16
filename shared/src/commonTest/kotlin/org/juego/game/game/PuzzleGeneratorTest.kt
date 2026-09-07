package org.juego.game.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class PuzzleGeneratorTest {

    @Test
    fun generateMatchesDifficultyAndHasValidLineSums() {
        val generator = PuzzleGenerator(Random(42))
        Difficulty.entries.forEach { difficulty ->
            val puzzle = generator.generate(difficulty)

            assertEquals(difficulty, puzzle.difficulty)
            assertEquals(difficulty.givenCount, puzzle.given.count { it })
            assertEquals((1..CELL_COUNT).toSet(), puzzle.solution.toSet())
            assertEquals(rowSumsOf(puzzle.solution), puzzle.rowSums)
            assertEquals(colSumsOf(puzzle.solution), puzzle.colSums)
        }
    }

    @Test
    fun generateIsDeterministicForASeed() {
        val first = PuzzleGenerator(Random(99)).generate(Difficulty.Medium)
        val second = PuzzleGenerator(Random(99)).generate(Difficulty.Medium)

        assertEquals(first, second)
    }

    @Test
    fun generatedPuzzleHasAUniqueSolutionForEasySeed() {
        val puzzle = PuzzleGenerator(Random(1)).generate(Difficulty.Easy)
        val start = IntArray(CELL_COUNT) { index ->
            if (puzzle.given[index]) puzzle.solution[index] else 0
        }

        assertEquals(1, countSolutions(start, puzzle.rowSums, puzzle.colSums))
    }

    @Test
    fun rowAndColumnSumsMatchTheBoardLayout() {
        val solution = (1..16).toList()

        assertEquals(listOf(10, 26, 42, 58), rowSumsOf(solution))
        assertEquals(listOf(28, 32, 36, 40), colSumsOf(solution))
    }

    @Test
    fun countSolutionsFindsTheCompletedGridAsOneSolution() {
        val solution = (1..16).toList()
        val start = solution.toIntArray()

        assertEquals(1, countSolutions(start, rowSumsOf(solution), colSumsOf(solution)))
    }

    @Test
    fun countSolutionsReturnsZeroWhenAGivenBreaksTheSums() {
        val solution = (1..16).toList()
        val start = IntArray(CELL_COUNT)
        start[0] = 16

        assertEquals(0, countSolutions(start, rowSumsOf(solution), colSumsOf(solution)))
    }

    @Test
    fun countSolutionsDetectsMoreThanOneCompletion() {
        // These sums admit two completions by swapping the top-left 2x2:
        // 1 2    vs    2 1
        // 6 5          5 6
        val solution = listOf(
            1, 2, 4, 3,
            6, 5, 7, 8,
            9, 10, 11, 12,
            13, 14, 15, 16,
        )
        val start = solution.toIntArray()
        start[0] = 0
        start[1] = 0
        start[4] = 0
        start[5] = 0

        val solutions = countSolutions(start, rowSumsOf(solution), colSumsOf(solution), limit = 2)
        assertEquals(2, solutions)
    }

    @Test
    fun countSolutionsStopsAfterNodeBudget() {
        val solution = (1..16).toList()
        val empty = IntArray(CELL_COUNT)

        val solutions = countSolutions(
            start = empty,
            rowSums = rowSumsOf(solution),
            colSums = colSumsOf(solution),
            limit = 2,
            maxNodes = 1,
        )
        assertEquals(2, solutions)
    }
}
