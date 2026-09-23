package org.juego.game.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.app_name
import kotlinproject.shared.generated.resources.continue_game
import kotlinproject.shared.generated.resources.continue_game_paused
import kotlinproject.shared.generated.resources.empty_value
import kotlinproject.shared.generated.resources.games_completed
import kotlinproject.shared.generated.resources.how_to_play_title
import kotlinproject.shared.generated.resources.juego16_header
import kotlinproject.shared.generated.resources.new_game
import kotlinproject.shared.generated.resources.play
import kotlinproject.shared.generated.resources.recent_times
import kotlinproject.shared.generated.resources.stat_average
import kotlinproject.shared.generated.resources.stat_best_time
import kotlinproject.shared.generated.resources.stat_completed
import kotlinproject.shared.generated.resources.your_stats
import org.juego.game.local.model.DifficultyStats
import org.juego.game.local.model.SavedGameSummary
import org.juego.game.local.model.UserStats
import org.juego.game.game.Difficulty
import org.juego.game.presentation.HomeViewModel
import org.juego.game.ui.PaperBackground
import org.juego.game.ui.dialog.HowToPlayDialog
import org.juego.game.ui.dialog.formatTime
import org.juego.game.ui.localizedName
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onContinueGame: () -> Unit = {},
    onPlay: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val savedGame by viewModel.savedGame.collectAsStateWithLifecycle()
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsStateWithLifecycle()
    var showHowToPlayManually by remember { mutableStateOf(false) }

    HomeScreenContent(
        stats = stats,
        savedGame = savedGame,
        showHowToPlay = isFirstLaunch == true || showHowToPlayManually,
        onContinueGame = onContinueGame,
        onPlay = onPlay,
        onShowHowToPlay = { showHowToPlayManually = true },
        onDismissHowToPlay = {
            showHowToPlayManually = false
            viewModel.dismissHowToPlay()
        },
    )
}

@Composable
internal fun HomeScreenContent(
    stats: UserStats,
    savedGame: SavedGameSummary?,
    showHowToPlay: Boolean,
    onContinueGame: () -> Unit,
    onPlay: () -> Unit,
    onShowHowToPlay: () -> Unit,
    onDismissHowToPlay: () -> Unit,
) {
    HowToPlayDialog(
        visible = showHowToPlay,
        onDismiss = onDismissHowToPlay,
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground),
    ) {
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .heightIn(max = 80.dp),
            painter = painterResource(Res.drawable.juego16_header),
            contentDescription = stringResource(Res.string.app_name),
            contentScale = ContentScale.Fit,
        )
        Text(
            text = stringResource(Res.string.your_stats),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.games_completed, stats.totalGamesFinished),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(20.dp))

        Difficulty.entries.forEach { difficulty ->
            DifficultyStatsCard(
                stats = stats.forDifficulty(difficulty),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            )
        }

        Spacer(Modifier.height(8.dp))

        val inProgressGame = savedGame?.takeUnless { it.isSolved }
        if (inProgressGame != null) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(2000)),
                exit = fadeOut(tween(2000))
            ) {
                Button(
                    onClick = onContinueGame,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    val difficultyName = inProgressGame.difficulty.localizedName()
                    val time = formatTime(inProgressGame.elapsedSeconds)
                    Text(
                        if (inProgressGame.isPaused) {
                            stringResource(Res.string.continue_game_paused, difficultyName, time)
                        } else {
                            stringResource(Res.string.continue_game, difficultyName, time)
                        }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        OutlinedButton(
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(stringResource(if (inProgressGame != null) Res.string.new_game else Res.string.play))
        }

            Spacer(Modifier.height(110.dp))
        }

        IconButton(
            onClick = onShowHowToPlay,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp)
                .padding(4.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(Res.string.how_to_play_title),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
@Composable
private fun DifficultyStatsCard(
    stats: DifficultyStats,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stats.difficulty.localizedName(),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            StatRow(stringResource(Res.string.stat_completed), stats.gamesCompleted.toString())
            StatRow(
                stringResource(Res.string.stat_best_time),
                stats.bestElapsedSeconds?.let(::formatTime) ?: stringResource(Res.string.empty_value),
            )
            StatRow(
                stringResource(Res.string.stat_average),
                stats.averageElapsedSeconds?.let(::formatTime) ?: stringResource(Res.string.empty_value),
            )
            if (stats.completionTimesSeconds.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        Res.string.recent_times,
                        stats.completionTimesSeconds.take(1).joinToString(" · ", transform = ::formatTime),
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}