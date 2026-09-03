package org.juego.game

import android.app.Application
import org.juego.game.local.AndroidAppContext

class JuegoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidAppContext.initialize(this)
    }
}