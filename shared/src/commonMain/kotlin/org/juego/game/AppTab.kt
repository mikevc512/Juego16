package org.juego.game

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.tab_game
import kotlinproject.shared.generated.resources.tab_home
import org.jetbrains.compose.resources.stringResource

enum class AppTab(val icon: ImageVector) {
    HOME(Icons.Default.Home),
    GAME(Icons.Default.Add);

    @Composable
    fun label(): String = stringResource(
        when (this) {
            HOME -> Res.string.tab_home
            GAME -> Res.string.tab_game
        }
    )
}