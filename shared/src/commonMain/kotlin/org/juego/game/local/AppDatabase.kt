package org.juego.game.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import org.juego.game.local.dao.CompletedGameDao
import org.juego.game.local.dao.SavedGameDao
import org.juego.game.local.model.CompletedGameEntity
import org.juego.game.local.model.SavedGameEntity

@Database(
    entities = [CompletedGameEntity::class, SavedGameEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun completedGameDao(): CompletedGameDao
    abstract fun savedGameDao(): SavedGameDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
