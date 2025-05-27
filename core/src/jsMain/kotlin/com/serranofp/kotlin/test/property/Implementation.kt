@file:Suppress("INVISIBLE_REFERENCE")
package com.serranofp.kotlin.test.property

import kotlin.random.Random
import kotlin.test.adapter

@Target(AnnotationTarget.FUNCTION)
public actual annotation class PropertyTest

public actual typealias PropertyTestResult = Unit

public actual fun propertyTest(
    iterations: Int,
    context: GeneratorContext,
    seed: Int?,
    continueAfterFirstFailure: Boolean,
    block: PropertyRunner.() -> Unit
) {
    val seed = seed ?: Random.nextInt(from = 0, until = Int.MAX_VALUE)
    val random = Random(seed)
    with(adapter()) {
        suite("seed $seed", ignored = false) {
            for(i in 1 .. iterations) {
                val context = PropertyRunner(context, random)
                val result = runCatching { block(context) }
                val name = context.values.joinToString(separator = ", ") { "${it.first.name}: ${it.second}" }
                test(name, ignored = false) { result.getOrThrow() }
                if (result.isFailure && !continueAfterFirstFailure) break
            }
        }
    }
}