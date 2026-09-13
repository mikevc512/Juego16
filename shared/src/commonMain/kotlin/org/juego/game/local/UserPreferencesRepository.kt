package org.juego.game.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
    private val ioDispatcher: CoroutineDispatcher,
) {
    fun observeIsFirstLaunch(): Flow<Boolean> =
        dataStore.data
            .map { preferences -> preferences[HAS_SEEN_HOW_TO_PLAY] != true }
            .flowOn(ioDispatcher)

    suspend fun markHowToPlaySeen() = withContext(ioDispatcher) {
        dataStore.edit { preferences ->
            preferences[HAS_SEEN_HOW_TO_PLAY] = true
        }
    }

    private companion object {
        val HAS_SEEN_HOW_TO_PLAY = booleanPreferencesKey("has_seen_how_to_play")
    }
}
