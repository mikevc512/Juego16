package org.juego.game

import androidx.compose.runtime.MutableState

interface NetworkConnection {
    fun connection(): MutableState<Connection>
}

enum class Connection {
    CONNECTED,
    DISCONNECTED
}