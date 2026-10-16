package org.juego.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation3.runtime.rememberNavBackStack
import org.juego.game.di.koinAppConfiguration
import org.juego.game.presentation.GameIntent
import org.juego.game.presentation.GameViewModel
import org.juego.game.ui.BoardCream
import org.juego.game.ui.Coral
import org.juego.game.ui.CorrectGreen
import org.juego.game.ui.JuegoTheme
import org.juego.game.ui.navigation.GameRoute
import org.juego.game.ui.navigation.HomeRoute
import org.juego.game.ui.navigation.navSerializationConfig
import org.juego.game.ui.PaperBackground
import org.juego.game.ui.navigation.JuegoNavDisplay
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinApplication(configuration = koinAppConfiguration) {
        JuegoTheme {
            val viewModel = koinViewModel<GameViewModel>()
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

            Scaffold(
                modifier = Modifier.fillMaxSize().background(PaperBackground),
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
            ) { padding ->
                JuegoNavDisplay(
                    modifier = Modifier.padding(padding),
                    backStack = backStack,
                    selectTab = { selectedTab ->
                        selectTab(selectedTab)
                    }
                )
            }
        }
    }
}