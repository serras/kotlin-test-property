package com.serranofp.kotlin.test.property

import kotlin.random.Random

public fun interface Generator<out T> {
    public fun next(random: Random): T
}

public interface GeneratorContext {
    public val size: Int

    public companion object {
        public val Default: GeneratorContext = object : GeneratorContext {
            override val size: Int = 100
        }
    }
}