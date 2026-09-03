package org.juego.game.local.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "saved_game")
data class SavedGameEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val difficulty: String,
    val solutionCsv: String,
    val givenCsv: String,
    val rowSumsCsv: String,
    val colSumsCsv: String,
    val entriesCsv: String,
    val historyCsv: String,
    val selectedIndex: Int?,
    val elapsedSeconds: Int,
    val isSolved: Boolean,
    val isFilled: Boolean,
    val showingErrors: Boolean,
    val isResultDialogDismissed: Boolean,
    val isPaused: Boolean,
    val savedAtEpochMs: Long,
) {
    companion object {
        const val SINGLETON_ID = 0
    }
}
