package org.juego.game.game

data class GameState(
    val puzzle: Puzzle,
    val entries: List<Int>,
    val selectedIndex: Int? = null,
    val history: List<List<Int>> = emptyList(),
    val isSolved: Boolean = false,
    val isFilled: Boolean = false,
    val showingErrors: Boolean = false
) {
    val analysis: BoardAnalysis
        get() = analyzeBoard(entries, puzzle.rowSums, puzzle.colSums)

    fun select(index: Int): GameState {
        require(index in 0 until CELL_COUNT)
        return copy(selectedIndex = index, showingErrors = false)
    }

    fun enter(number: Int): GameState {
        require(number in 1..CELL_COUNT)
        val index = selectedIndex ?: return this
        if (puzzle.given[index]) return this
        if (entries[index] == number) return erase()
        return commit(entries.toMutableList().also { it[index] = number })
    }

    fun erase(): GameState {
        val index = selectedIndex ?: return this
        if (puzzle.given[index] || entries[index] == 0) return this
        return commit(entries.toMutableList().also { it[index] = 0 })
    }

    fun undo(): GameState {
        if (history.isEmpty()) return this
        val previous = history.last()
        return copy(
            entries = previous,
            history = history.dropLast(1),
            isSolved = false,
            showingErrors = false,
        )
    }

    fun reset(): GameState = copy(
        entries = puzzle.initialEntries(),
        selectedIndex = null,
        history = emptyList(),
        isSolved = false,
        showingErrors = false,
    )

    fun check(): GameState {
        return copy(showingErrors = true)
    }

    fun canUndo(): Boolean {
        if (this.isSolved) {
            return false
        }
        return history.isNotEmpty()
    }

    fun canErase(): Boolean {
        if (this.isSolved) return false
        val index = selectedIndex ?: return false
        return !puzzle.given[index] && entries[index] != 0
    }

    private fun commit(nextEntries: List<Int>): GameState {
        val nextAnalysis = analyzeBoard(nextEntries, puzzle.rowSums, puzzle.colSums)
        return copy(
            entries = nextEntries,
            history = history + listOf(entries),
            isSolved = nextAnalysis.isSolved,
            isFilled = nextAnalysis.isFilled,
            showingErrors = false,
        )
    }

    companion object {
        fun newGame(
            difficulty: Difficulty,
            generator: PuzzleGenerator = PuzzleGenerator(),
        ): GameState {
            val puzzle = generator.generate(difficulty)
            return GameState(
                puzzle = puzzle,
                entries = puzzle.initialEntries(),
            )
        }
    }
}