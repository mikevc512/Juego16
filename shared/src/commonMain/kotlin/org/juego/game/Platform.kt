package org.juego.game

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform