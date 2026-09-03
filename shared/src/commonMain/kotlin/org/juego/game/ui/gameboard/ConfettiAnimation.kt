package org.juego.game.ui.gameboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Rotation
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun ConfettiAnimation() {
    ConfettiKit(
        modifier = Modifier.fillMaxSize().background(Color.Transparent),
        parties = listOf(
            Party(
                timeToLive = 4000,       // 3 seconds lifetime
                fadeOutEnabled = true,   // Fade out at end
                delay = 200,             // Start after 500ms
                position = Position.Relative(0.5, 0.2),
                rotation = Rotation.enabled(),
                emitter = Emitter(duration = 3.toDuration(DurationUnit.SECONDS)).max(
                    100
                )
            )
        ),
        onParticleSystemStarted = { system, activeSystems ->
            // Called when a party animation starts
        },
        onParticleSystemEnded = { system, activeSystems ->
            // Called when a party animation ends
        }
    )
}