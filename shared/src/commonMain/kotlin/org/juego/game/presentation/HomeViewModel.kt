package org.juego.game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.juego.game.local.GameRepository
import org.juego.game.local.model.SavedGameSummary
import org.juego.game.local.model.UserStats

class HomeViewModel(
    repository: GameRepository,
) : ViewModel() {
    val stats: StateFlow<UserStats> = repository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserStats.Empty)

    val savedGame: StateFlow<SavedGameSummary?> = repository.observeSavedGame()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}