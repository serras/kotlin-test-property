package com.serranofp.kotlin.test.property

import kotlin.test.Test
import kotlin.test.assertTrue

sealed interface Example {
    data class Number(val number: Int): Example
    data class Thing(val string: String): Example

    companion object {
        context(_: GeneratorContext)
        val generator: Generator<Example>
            get() = choice(
                2 to ::Number.by(int()),
                3 to ::Thing.by(string())
            )
    }
}

class ExampleTests {
    @Test @PropertyTest
    fun example1() = propertyTest {
        val pair by pair(int(), int(1, 50))
        @Suppress("UNUSED_VARIABLE")
        val other by list(int() or string(), size = 3)
        assertTrue(pair.second > 0)
    }

    @Test @PropertyTest
    fun example2() = propertyTest {
        val example by Example.generator
        assertTrue(example is Example.Number || example is Example.Thing)
    }
}