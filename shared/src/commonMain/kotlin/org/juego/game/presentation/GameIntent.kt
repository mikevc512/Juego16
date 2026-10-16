package org.juego.game.presentation

import org.juego.game.game.Difficulty

sealed interface GameIntent {
    data class SelectCell(val index: Int) : GameIntent
    data class EnterNumber(val number: Int) : GameIntent
    data object Erase : GameIntent
    data object Undo : GameIntent
    data object Reset : GameIntent
    data object Check : GameIntent
    data class NewGame(val difficulty: Difficulty) : GameIntent
    data object DismissResultDialog : GameIntent
    data object DismissSuccessDialog : GameIntent
    data object Pause : GameIntent
    data object EndGame : GameIntent
    data object Resume : GameIntent
    data object ShowGameSelector : GameIntent
    data object AppBackgrounded : GameIntent
    data object AppForegrounded : GameIntent
    data class SetGameScreenVisible(val visible: Boolean) : GameIntent
}