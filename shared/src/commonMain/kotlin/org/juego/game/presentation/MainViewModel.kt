package org.juego.game.presentation

import androidx.lifecycle.ViewModel
import org.juego.game.game.PuzzleGenerator


class MainViewModel(
    private val generator: PuzzleGenerator,
) : ViewModel() {
//    private val _state = MutableStateFlow(HomeUiState())
//    val state: StateFlow<HomeUiState> = _state
//
//    fun selectTab(tab: HomeTab) {
//        _state.update { it.copy(selectedTab = tab) }
//    }
}