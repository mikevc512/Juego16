package org.juego.game.game

import kotlin.random.Random

private const val MAX_GAME_CREATE_ATTEMPTS = 120
class PuzzleGenerator(private val random: Random = Random.Default) {
    fun generate(difficulty: Difficulty): Puzzle {
        return createCheckedGame(difficulty) ?: generateUnchecked(difficulty)
    }

    /**
     * Creates verified one solution game with provided difficulty, it attempts up to 120 times
     * before it defaults to just creating a game that is not verified to have one answer
     */
    private fun createCheckedGame(difficulty: Difficulty) : Puzzle? {
        repeat(MAX_GAME_CREATE_ATTEMPTS) {
            val solution = (1..CELL_COUNT).shuffled(random)
            val rowSums = rowSumsOf(solution)
            val colSums = colSumsOf(solution)
            val given = BooleanArray(CELL_COUNT) { true }
            val removalOrder = (0 until CELL_COUNT).shuffled(random)

            for (index in removalOrder) {
                if (given.count { it } <= difficulty.givenCount) break
                given[index] = false
                val start = IntArray(CELL_COUNT) { cell -> if (given[cell]) solution[cell] else 0 }
                if (countSolutions(start, rowSums, colSums) != 1) {
                    given[index] = true
                }
            }

            if (given.count { it } == difficulty.givenCount) {
                return toPuzzle(solution, given, rowSums, colSums, difficulty)
            }
        }
        return null
    }

    private fun generateUnchecked(difficulty: Difficulty): Puzzle {
        val solution = (1..CELL_COUNT).shuffled(random)
        val givenIndices = (0 until CELL_COUNT)
            .shuffled(random)
            .take(difficulty.givenCount)
            .toSet()
        val given = BooleanArray(CELL_COUNT) { it in givenIndices }
        return toPuzzle(solution, given, rowSumsOf(solution), colSumsOf(solution), difficulty)
    }

    private fun toPuzzle(
        solution: List<Int>,
        given: BooleanArray,
        rowSums: List<Int>,
        colSums: List<Int>,
        difficulty: Difficulty,
    ) = Puzzle(
        solution = solution,
        given = given.toList(),
        rowSums = rowSums,
        colSums = colSums,
        difficulty = difficulty,
    )
}

internal fun rowSumsOf(solution: List<Int>): List<Int> =
    (0 until BOARD_SIZE).map { row ->
        (0 until BOARD_SIZE).sumOf { col -> solution[row * BOARD_SIZE + col] }
    }

internal fun colSumsOf(solution: List<Int>): List<Int> =
    (0 until BOARD_SIZE).map { col ->
        (0 until BOARD_SIZE).sumOf { row -> solution[row * BOARD_SIZE + col] }
    }

internal fun countSolutions(
    start: IntArray,
    rowSums: List<Int>,
    colSums: List<Int>,
    limit: Int = 2,
    maxNodes: Int = 250_000,
): Int {
    val grid = start.copyOf()
    val used = BooleanArray(CELL_COUNT + 1)
    val rowSum = IntArray(BOARD_SIZE)
    val colSum = IntArray(BOARD_SIZE)
    val rowCount = IntArray(BOARD_SIZE)
    val colCount = IntArray(BOARD_SIZE)

    for (index in 0 until CELL_COUNT) {
        val value = grid[index]
        if (value != 0) {
            used[value] = true
            val row = index / BOARD_SIZE
            val col = index % BOARD_SIZE
            rowSum[row] += value
            colSum[col] += value
            rowCount[row]++
            colCount[col]++
        }
    }

    var count = 0
    var nodes = 0

    fun canPlace(value: Int, row: Int, col: Int): Boolean {
        if (value !in 1..CELL_COUNT || used[value]) return false
        val nextRowSum = rowSum[row] + value
        val nextColSum = colSum[col] + value
        val nextRowCount = rowCount[row] + 1
        val nextColCount = colCount[col] + 1
        if (nextRowCount == BOARD_SIZE && nextRowSum != rowSums[row]) return false
        if (nextColCount == BOARD_SIZE && nextColSum != colSums[col]) return false
        if (nextRowCount < BOARD_SIZE) {
            val remainingCells = BOARD_SIZE - nextRowCount
            val remainingSum = rowSums[row] - nextRowSum
            if (remainingSum < remainingCells || remainingSum > remainingCells * CELL_COUNT) return false
        }
        if (nextColCount < BOARD_SIZE) {
            val remainingCells = BOARD_SIZE - nextColCount
            val remainingSum = colSums[col] - nextColSum
            if (remainingSum < remainingCells || remainingSum > remainingCells * CELL_COUNT) return false
        }
        return true
    }

    fun place(index: Int, value: Int, row: Int, col: Int) {
        grid[index] = value
        used[value] = true
        rowSum[row] += value
        colSum[col] += value
        rowCount[row]++
        colCount[col]++
    }

    fun unplace(index: Int, value: Int, row: Int, col: Int) {
        grid[index] = 0
        used[value] = false
        rowSum[row] -= value
        colSum[col] -= value
        rowCount[row]--
        colCount[col]--
    }

    fun dfs(index: Int): Boolean {
        if (count >= limit) return true
        if (++nodes > maxNodes) {
            count = limit
            return true
        }
        if (index == CELL_COUNT) {
            count++
            return count >= limit
        }
        if (grid[index] != 0) return dfs(index + 1)

        val row = index / BOARD_SIZE
        val col = index % BOARD_SIZE
        val rowLeft = BOARD_SIZE - rowCount[row]
        val colLeft = BOARD_SIZE - colCount[col]
        val rowRemaining = rowSums[row] - rowSum[row]
        val colRemaining = colSums[col] - colSum[col]

        val forced = when {
            rowLeft == 1 && colLeft == 1 -> {
                if (rowRemaining != colRemaining) return false
                rowRemaining
            }
            rowLeft == 1 -> rowRemaining
            colLeft == 1 -> colRemaining
            else -> null
        }

        if (forced != null) {
            if (!canPlace(forced, row, col)) return false
            place(index, forced, row, col)
            val stop = dfs(index + 1)
            unplace(index, forced, row, col)
            return stop
        }

        for (value in 1..CELL_COUNT) {
            if (!canPlace(value, row, col)) continue
            place(index, value, row, col)
            val stop = dfs(index + 1)
            unplace(index, value, row, col)
            if (stop) return true
        }
        return false
    }

    dfs(0)
    return count
}
