package org.juego.game.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import org.juego.game.local.model.CompletedGameEntity

@Dao
interface CompletedGameDao {
    @Insert
    suspend fun insert(game: CompletedGameEntity)

    @Query("SELECT * FROM completed_games ORDER BY completedAtEpochMs DESC")
    fun observeAll(): Flow<List<CompletedGameEntity>>

    @Query("SELECT * FROM completed_games ORDER BY completedAtEpochMs DESC")
    suspend fun getAll(): List<CompletedGameEntity>
}
