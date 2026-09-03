package org.juego.game.local.model

import org.juego.game.game.Difficulty

data class UserStats(
    val byDifficulty: Map<Difficulty, DifficultyStats> =
        Difficulty.entries.associateWith { DifficultyStats(it) },
) {
    val totalGamesFinished: Int
        get() = byDifficulty.values.sumOf { it.gamesCompleted }

    fun forDifficulty(difficulty: Difficulty): DifficultyStats =
        byDifficulty[difficulty] ?: DifficultyStats(difficulty)

    companion object {
        val Empty = UserStats()
    }
}

data class DifficultyStats(
    val difficulty: Difficulty,
    val gamesCompleted: Int = 0,
    val completionTimesSeconds: List<Int> = emptyList(),
) {
    val bestElapsedSeconds: Int?
        get() = completionTimesSeconds.minOrNull()

    val averageElapsedSeconds: Int?
        get() = completionTimesSeconds.takeIf { it.isNotEmpty() }?.average()?.toInt()
}

data class SavedGameSummary(
    val difficulty: Difficulty,
    val elapsedSeconds: Int,
    val isPaused: Boolean,
    val isSolved: Boolean,
)
