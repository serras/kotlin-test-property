package com.serranofp.kotlin.test.property

import kotlin.test.Test
import kotlin.test.assertTrue

class Example {
    @Test @PropertyTest
    fun example() = propertyTest {
        val pair by pair(int(), int(1, 50))
        @Suppress("UNUSED_VARIABLE")
        val other by list(int() or string(), size = 3)
        assertTrue(pair.second > 0)
    }
}