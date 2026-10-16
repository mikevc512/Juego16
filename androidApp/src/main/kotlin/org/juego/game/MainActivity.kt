package org.juego.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.juego.game.local.AndroidAppContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        AndroidAppContext.initialize(this)

        enableEdgeToEdge()

        setContent {
            App()
        }
    }
}