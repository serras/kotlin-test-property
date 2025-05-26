package com.serranofp.kotlin.test.property

import com.github.curiousoddman.rgxgen.RgxGen
import com.github.curiousoddman.rgxgen.config.RgxGenOption
import com.github.curiousoddman.rgxgen.config.RgxGenProperties
import kotlin.random.asJavaRandom

context(ctx: GeneratorContext)
public fun regex(regex: String, size: Int = ctx.size): Generator<String> {
    val options = RgxGenProperties().apply {
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(this, size)
    }
    return regex(RgxGen.parse(options, regex))
}

context(ctx: GeneratorContext)
public fun regex(regex: RgxGen): Generator<String> = Generator { regex.generate(it.asJavaRandom()) }