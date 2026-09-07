package org.juego.game

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import org.juego.game.di.koinAppConfiguration
import org.juego.game.game.Difficulty
import org.juego.game.presentation.GameIntent
import org.juego.game.presentation.GameViewModel
import org.juego.game.ui.BoardCream
import org.juego.game.ui.Coral
import org.juego.game.ui.CorrectGreen
import org.juego.game.ui.KubokTheme
import org.juego.game.ui.gameboard.GameScreen
import org.juego.game.ui.home.HomeScreen
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.tab_game
import kotlinproject.shared.generated.resources.tab_home
import org.juego.game.ui.navigation.GameRoute
import org.juego.game.ui.navigation.HomeRoute
import org.juego.game.ui.navigation.navSerializationConfig
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinApplication(configuration = koinAppConfiguration) {
        KubokTheme {
            val viewModel = koinViewModel<GameViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val backStack = rememberNavBackStack(navSerializationConfig, HomeRoute)
            var activeTab by remember { mutableStateOf(AppTab.HOME) }

            fun selectTab(tab: AppTab) {
                activeTab = tab
                when (tab) {
                    AppTab.HOME -> {
                        while (backStack.lastOrNull() != HomeRoute && backStack.isNotEmpty()) {
                            backStack.removeLastOrNull()
                        }
                        if (backStack.isEmpty()) backStack.add(HomeRoute)
                    }

                    AppTab.GAME -> if (backStack.lastOrNull() != GameRoute) {
                        backStack.add(GameRoute)
                    }
                }
            }

            LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
                viewModel.onIntent(GameIntent.AppBackgrounded)
            }
            LifecycleEventEffect(Lifecycle.Event.ON_START) {
                viewModel.onIntent(GameIntent.AppForegrounded)
            }

            //modifier = Modifier.height(110.dp),
            Scaffold(
                bottomBar = {
                    NavigationBar( containerColor = BoardCream) {
                        AppTab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = activeTab == tab,
                                onClick = { selectTab(tab) },
                                icon = { Icon(tab.icon, contentDescription = tab.label()) },
                                label = {
                                    Text(tab.label())
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Coral,
                                    unselectedIconColor = CorrectGreen,
                                    indicatorColor = Color.Transparent // Removes the default oval capsule highlight
                                )
                            )
                        }
                    }
                }
            ) {
                NavDisplay(
                    backStack = backStack,
                    onBack = {
                        backStack.removeLastOrNull()
                        activeTab =
                            if (backStack.lastOrNull() is GameRoute) AppTab.GAME else AppTab.HOME
                    },
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(600)
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { -it / 4 },
                            animationSpec = tween(300)
                        )
                    },
                    popTransitionSpec = {
                        fadeIn(tween(500)) + slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(500),
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(500),
                        ) + fadeOut(tween(500))
                    },
                    predictivePopTransitionSpec = {
                        slideInHorizontally(
                            animationSpec = tween(500),
                            initialOffsetX = { -it / 4 },
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(500),
                            targetOffsetX = { it },
                        )
                    },
                    entryProvider = entryProvider {
                        entry<HomeRoute> {
                            HomeScreen(
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
        }
    }
}

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