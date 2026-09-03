package org.juego.game.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.juego.game.local.GameRepository
import org.juego.game.local.getRoomDatabase
import org.juego.game.local.AppDatabase
import org.juego.game.game.PuzzleGenerator
import org.juego.game.presentation.GameViewModel
import org.juego.game.presentation.HomeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.koinConfiguration
import org.koin.dsl.module
import kotlin.random.Random

val appModule = module {
    singleOf(::PuzzleGenerator)
    single<Random> { Random.Default }
    single<AppDatabase> { getRoomDatabase() }
    single {
        GameRepository(
            database = get(),
            ioDispatcher = get(named("IO"))
        )
    }
    viewModelOf(::GameViewModel)
    viewModelOf(::HomeViewModel)
}

val coroutineModule = module {
    single<CoroutineDispatcher>(named("IO")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("Default")) { Dispatchers.Default }
    single<CoroutineDispatcher>(named("Main")) { Dispatchers.Main }
}

val koinAppConfiguration = koinConfiguration {
    modules(appModule)
    modules(coroutineModule)
}
