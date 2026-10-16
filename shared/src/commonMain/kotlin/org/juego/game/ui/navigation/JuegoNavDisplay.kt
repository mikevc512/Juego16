package org.juego.game.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.juego.game.AppTab
import org.juego.game.presentation.GameViewModel
import org.juego.game.ui.gameboard.GameScreen
import org.juego.game.ui.home.HomeScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun JuegoNavDisplay(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    selectTab: (AppTab) -> Unit
) {
    val viewModel = koinViewModel<GameViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
            selectTab(if (backStack.lastOrNull() is GameRoute) AppTab.GAME else AppTab.HOME)
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it / 4 },
                animationSpec = tween(300)
            )
        },
        popTransitionSpec = {
            fadeIn(tween(300)) + slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300),
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300),
            ) + fadeOut(tween(300))
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { -it / 4 },
            ) togetherWith slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { it },
            )
        },
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(
                    modifier = modifier,
                    onContinueGame = { selectTab(AppTab.GAME) },
                    onIntent = {
                        viewModel.onIntent(it)
                        selectTab(AppTab.GAME)
                    },
                )
            }
            entry<GameRoute> {
                GameScreen(
                    uiState = uiState,
                    onIntent = viewModel::onIntent,
                )
            }
        }
    )
}