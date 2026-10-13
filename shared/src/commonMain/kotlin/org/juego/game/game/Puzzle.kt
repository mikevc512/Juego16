package org.juego.game.game

const val BOARD_SIZE = 4
const val CELL_COUNT = BOARD_SIZE * BOARD_SIZE

data class Puzzle(
    val solution: List<Int>,
    val given: List<Boolean>,
    val rowSums: List<Int>,
    val colSums: List<Int>,
    val difficulty: Difficulty,
) {
    init {
        require(solution.size == CELL_COUNT)
        require(given.size == CELL_COUNT)
        require(rowSums.size == BOARD_SIZE)
        require(colSums.size == BOARD_SIZE)
        require(solution.toSet() == (1..CELL_COUNT).toSet()) {
            "Solution must contain each number from 1 to 16 once"
        }
        require(given.count { it } == difficulty.givenCount) {
            "Expected ${difficulty.givenCount} given numbers for ${difficulty.displayName}"
        }
        repeat(BOARD_SIZE) { row ->
            val sum = (0 until BOARD_SIZE).sumOf { col -> solution[row * BOARD_SIZE + col] }
            require(sum == rowSums[row])
        }
        repeat(BOARD_SIZE) { col ->
            val sum = (0 until BOARD_SIZE).sumOf { row -> solution[row * BOARD_SIZE + col] }
            require(sum == colSums[col])
        }
    }

    fun initialEntries(): List<Int> =
        solution.mapIndexed { index, value -> if (given[index]) value else 0 }
}

enum class LineState {
    Incomplete,
    Correct,
    Incorrect,
}

data class BoardAnalysis(
    val duplicateIndices: Set<Int>,
    val rowStates: List<LineState>,
    val colStates: List<LineState>,
    val usedNumbers: Set<Int>,
    val isFilled: Boolean,
    val isSolved: Boolean,
)

fun analyzeBoard(
    entries: List<Int>,
    rowSums: List<Int>,
    colSums: List<Int>,
): BoardAnalysis {
    require(entries.size == CELL_COUNT)

    val counts = IntArray(CELL_COUNT + 1)
    entries.forEach { value ->
        if (value in 1..CELL_COUNT) counts[value]++
    }
    val duplicateIndices = entries.mapIndexedNotNull { index, value ->
        index.takeIf { value in 1..CELL_COUNT && counts[value] > 1 }
    }.toSet()

    val rowStates = (0 until BOARD_SIZE).map { row ->
        val values = (0 until BOARD_SIZE).map { col -> entries[row * BOARD_SIZE + col] }
        lineState(values, rowSums[row])
    }
    val colStates = (0 until BOARD_SIZE).map { col ->
        val values = (0 until BOARD_SIZE).map { row -> entries[row * BOARD_SIZE + col] }
        lineState(values, colSums[col])
    }

    val usedNumbers = entries.filter { it in 1..CELL_COUNT }.toSet()
    val isFilled = entries.all { it in 1..CELL_COUNT }
    val isSolved = isFilled &&
        usedNumbers.size == CELL_COUNT &&
        duplicateIndices.isEmpty() &&
        rowStates.all { it == LineState.Correct } &&
        colStates.all { it == LineState.Correct }

    return BoardAnalysis(
        duplicateIndices = duplicateIndices,
        rowStates = rowStates,
        colStates = colStates,
        usedNumbers = usedNumbers,
        isFilled = isFilled,
        isSolved = isSolved,
    )
}

private fun lineState(values: List<Int>, target: Int): LineState {
    if (values.any { it == 0 }) return LineState.Incomplete
    val sumMatches = values.sum() == target
    val unique = values.toSet().size == BOARD_SIZE
    return if (sumMatches && unique) LineState.Correct else LineState.Incorrect
}
