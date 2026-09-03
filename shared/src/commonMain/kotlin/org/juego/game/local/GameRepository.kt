package org.juego.game.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.juego.game.local.model.SavedGameSummary
import org.juego.game.local.model.UserStats
import org.juego.game.game.Difficulty
import org.juego.game.presentation.GameUiState

class GameRepository(
    private val database: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val completedGameDao = database.completedGameDao()
    private val savedGameDao = database.savedGameDao()
    private val mutex = Mutex()

    fun observeStats(): Flow<UserStats> =
        completedGameDao.observeAll().map(GamePersistenceMapper::toUserStats).flowOn(ioDispatcher)

    fun observeSavedGame(): Flow<SavedGameSummary?> =
        savedGameDao.observe().map { entity ->
            entity?.let(GamePersistenceMapper::toSummary)
        }.flowOn(ioDispatcher)

    suspend fun loadSavedGame(): GameUiState? = withContext(ioDispatcher) {
        mutex.withLock {
            val entity = savedGameDao.get() ?: return@withContext null
            runCatching { GamePersistenceMapper.toUiState(entity) }.getOrNull()
        }
    }

    suspend fun saveInProgressGame(state: GameUiState) = withContext(ioDispatcher) {
        if (state.isRestoring) return@withContext
        mutex.withLock {
            savedGameDao.upsert(
                GamePersistenceMapper.toSavedGameEntity(state, currentEpochMs()),
            )
        }
    }

    suspend fun recordCompletedGame(difficulty: Difficulty, elapsedSeconds: Int) = withContext(ioDispatcher) {
        mutex.withLock {
            completedGameDao.insert(
                GamePersistenceMapper.toCompletedGame(
                    difficulty = difficulty,
                    elapsedSeconds = elapsedSeconds,
                    completedAtEpochMs = currentEpochMs(),
                ),
            )
        }
    }

    suspend fun clearSavedGame() = withContext(ioDispatcher) {
        mutex.withLock {
            savedGameDao.clear()
        }
    }
}
