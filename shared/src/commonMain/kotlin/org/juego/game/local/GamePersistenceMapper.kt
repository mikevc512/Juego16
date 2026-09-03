package org.juego.game.local

import org.juego.game.local.model.CompletedGameEntity
import org.juego.game.local.model.DifficultyStats
import org.juego.game.local.model.SavedGameEntity
import org.juego.game.local.model.SavedGameSummary
import org.juego.game.local.model.UserStats
import org.juego.game.game.Difficulty
import org.juego.game.game.GameState
import org.juego.game.game.Puzzle
import org.juego.game.presentation.GameUiState

internal object GamePersistenceMapper {
    fun toSavedGameEntity(state: GameUiState, savedAtEpochMs: Long): SavedGameEntity {
        val puzzle = state.game.puzzle
        return SavedGameEntity(
            difficulty = puzzle.difficulty.name,
            solutionCsv = puzzle.solution.toCsv(),
            givenCsv = puzzle.given.toBooleanCsv(),
            rowSumsCsv = puzzle.rowSums.toCsv(),
            colSumsCsv = puzzle.colSums.toCsv(),
            entriesCsv = state.game.entries.toCsv(),
            historyCsv = state.game.history.toHistoryCsv(),
            selectedIndex = state.game.selectedIndex,
            elapsedSeconds = state.elapsedSeconds,
            isSolved = state.game.isSolved,
            isFilled = state.game.isFilled,
            showingErrors = state.game.showingErrors,
            isResultDialogDismissed = state.isResultDialogDismissed,
            isPaused = state.isPaused,
            savedAtEpochMs = savedAtEpochMs,
        )
    }

    fun toUiState(entity: SavedGameEntity): GameUiState {
        val difficulty = Difficulty.valueOf(entity.difficulty)
        val puzzle = Puzzle(
            solution = entity.solutionCsv.parseIntList(),
            given = entity.givenCsv.parseBooleanList(),
            rowSums = entity.rowSumsCsv.parseIntList(),
            colSums = entity.colSumsCsv.parseIntList(),
            difficulty = difficulty,
        )
        return GameUiState(
            game = GameState(
                puzzle = puzzle,
                entries = entity.entriesCsv.parseIntList(),
                selectedIndex = entity.selectedIndex,
                history = entity.historyCsv.parseHistory(),
                isSolved = entity.isSolved,
                isFilled = entity.isFilled,
                showingErrors = entity.showingErrors,
            ),
            elapsedSeconds = entity.elapsedSeconds,
            isResultDialogDismissed = entity.isResultDialogDismissed,
            isPaused = entity.isPaused,
            isRestoring = false,
        )
    }

    fun toSummary(entity: SavedGameEntity): SavedGameSummary = SavedGameSummary(
        difficulty = Difficulty.valueOf(entity.difficulty),
        elapsedSeconds = entity.elapsedSeconds,
        isPaused = entity.isPaused,
        isSolved = entity.isSolved,
    )

    fun toCompletedGame(
        difficulty: Difficulty,
        elapsedSeconds: Int,
        completedAtEpochMs: Long,
    ): CompletedGameEntity = CompletedGameEntity(
        difficulty = difficulty.name,
        elapsedSeconds = elapsedSeconds,
        completedAtEpochMs = completedAtEpochMs,
    )

    fun toUserStats(games: List<CompletedGameEntity>): UserStats {
        val grouped = games.groupBy { it.difficulty }
        return UserStats(
            byDifficulty = Difficulty.entries.associateWith { difficulty ->
                val ofDifficulty = grouped[difficulty.name].orEmpty()
                DifficultyStats(
                    difficulty = difficulty,
                    gamesCompleted = ofDifficulty.size,
                    completionTimesSeconds = ofDifficulty.map { it.elapsedSeconds },
                )
            },
        )
    }
}

internal fun List<Int>.toCsv(): String = joinToString(",")

internal fun List<Boolean>.toBooleanCsv(): String =
    joinToString(",") { if (it) "1" else "0" }

internal fun List<List<Int>>.toHistoryCsv(): String =
    joinToString(";") { it.toCsv() }

internal fun String.parseIntList(): List<Int> =
    if (isEmpty()) emptyList() else split(',').map { it.toInt() }

internal fun String.parseBooleanList(): List<Boolean> =
    if (isEmpty()) emptyList() else split(',').map { it == "1" }

internal fun String.parseHistory(): List<List<Int>> =
    if (isEmpty()) emptyList() else split(';').map { it.parseIntList() }
