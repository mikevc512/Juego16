package org.juego.game.local.model

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "completed_games",
    indices = [Index(value = ["difficulty"])],
)
data class CompletedGameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val difficulty: String,
    val elapsedSeconds: Int,
    val completedAtEpochMs: Long,
)
