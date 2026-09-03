package org.juego.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
private fun NoInternetScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = "No internet please turn on internet or upgrade to pro for offline access.")
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}