package com.serranofp.kotlin.test.property

import kotlin.test.Test
import kotlin.test.assertTrue

class Example {
    @Test
    fun example() = propertyTest {
        val pair by pair(int(), int(-1, 2))
        assertTrue(pair.second > 0)
    }
}