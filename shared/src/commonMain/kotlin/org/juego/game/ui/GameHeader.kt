package org.juego.game.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.app_name
import kotlinproject.shared.generated.resources.continue_game_paused
import kotlinproject.shared.generated.resources.elapsed_time
import org.juego.game.game.Difficulty
import org.juego.game.ui.dialog.formatTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameHeader(
    modifier: Modifier = Modifier,
    elapsedSeconds: Int,
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

        val time = formatTime(elapsedSeconds)
        val difficultyName = difficulty.localizedName()
        Text(
            text = stringResource(Res.string.elapsed_time, difficultyName, time),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace,
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