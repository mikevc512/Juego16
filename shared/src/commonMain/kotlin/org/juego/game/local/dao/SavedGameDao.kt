package org.juego.game.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import org.juego.game.local.model.SavedGameEntity

@Dao
interface SavedGameDao {
    @Upsert
    suspend fun upsert(game: SavedGameEntity)

    @Query("SELECT * FROM saved_game LIMIT 1")
    suspend fun get(): SavedGameEntity?

    @Query("SELECT * FROM saved_game LIMIT 1")
    fun observe(): Flow<SavedGameEntity?>

    @Query("DELETE FROM saved_game")
    suspend fun clear()
}