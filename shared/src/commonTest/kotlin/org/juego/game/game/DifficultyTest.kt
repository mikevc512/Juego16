package org.juego.game.game

import kotlin.test.Test
import kotlin.test.assertEquals

class DifficultyTest {

    @Test
    fun givenCountsDecreaseWithDifficulty() {
        assertEquals(6, Difficulty.Easy.givenCount)
        assertEquals(5, Difficulty.Medium.givenCount)
        assertEquals(4, Difficulty.Hard.givenCount)
    }

    @Test
    fun displayNamesMatchEnumNames() {
        Difficulty.entries.forEach { difficulty ->
            assertEquals(difficulty.name, difficulty.displayName)
        }
    }
}