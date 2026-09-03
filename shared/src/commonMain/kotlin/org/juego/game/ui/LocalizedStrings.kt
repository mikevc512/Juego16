package org.juego.game.ui

import androidx.compose.runtime.Composable
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.difficulty_easy
import kotlinproject.shared.generated.resources.difficulty_hard
import kotlinproject.shared.generated.resources.difficulty_medium
import org.juego.game.game.Difficulty
import org.jetbrains.compose.resources.stringResource

@Composable
fun Difficulty.localizedName(): String = stringResource(
    when (this) {
        Difficulty.Easy -> Res.string.difficulty_easy
        Difficulty.Medium -> Res.string.difficulty_medium
        Difficulty.Hard -> Res.string.difficulty_hard
    }
)
