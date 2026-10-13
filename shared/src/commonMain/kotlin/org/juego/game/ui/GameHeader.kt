package org.juego.game.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.app_name
import kotlinproject.shared.generated.resources.continue_game_paused
import kotlinproject.shared.generated.resources.elapsed_time
import kotlinx.coroutines.delay
import org.juego.game.game.Difficulty
import org.juego.game.local.currentEpochMs
import org.juego.game.presentation.elapsedSecondsAt
import org.juego.game.ui.dialog.formatTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameHeader(
    modifier: Modifier = Modifier,
    elapsedSeconds: Int,
    runningSinceEpochMs: Long?,
    difficulty: Difficulty,
    isPaused: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = modifier.padding(bottom = 8.dp),
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        ElapsedTimeLabel(
            elapsedSeconds = elapsedSeconds,
            runningSinceEpochMs = runningSinceEpochMs,
            difficulty = difficulty,
        )
        if (isPaused) {
            Text(
                modifier = modifier.padding(top = 8.dp),
                text =   stringResource(Res.string.continue_game_paused),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
private fun ElapsedTimeLabel(
    elapsedSeconds: Int,
    runningSinceEpochMs: Long?,
    difficulty: Difficulty,
) {
    var displayed by remember(elapsedSeconds, runningSinceEpochMs) {
        mutableIntStateOf(
            elapsedSecondsAt(elapsedSeconds, runningSinceEpochMs, currentEpochMs()),
        )
    }
    LaunchedEffect(elapsedSeconds, runningSinceEpochMs) {
        val since = runningSinceEpochMs ?: return@LaunchedEffect
        while (true) {
            val now = currentEpochMs()
            val elapsed = elapsedSecondsAt(elapsedSeconds, since, now)
            if (displayed != elapsed) displayed = elapsed
            val elapsedInSegment = (elapsed - elapsedSeconds).coerceAtLeast(0)
            val nextBoundary = since + (elapsedInSegment + 1) * 1_000L
            delay((nextBoundary - currentEpochMs()).coerceIn(16L, 1_000L))
        }
    }
    val time = formatTime(displayed)
    val difficultyName = difficulty.localizedName()
    Text(
        text = stringResource(Res.string.elapsed_time, difficultyName, time),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontFamily = FontFamily.Monospace,
    )
}