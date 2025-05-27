package com.serranofp.kotlin.test.property

import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.random.Random

public actual typealias PropertyTest = TestFactory
public actual typealias PropertyTestResult = DynamicContainer

public actual fun propertyTest(
    iterations: Int,
    context: GeneratorContext,
    seed: Int?,
    continueAfterFirstFailure: Boolean,
    block: PropertyRunner.() -> Unit
): DynamicContainer {
    val seed = seed ?: Random.nextInt(from = 0, until = Int.MAX_VALUE)
    val random = Random(seed)
    return dynamicContainer("seed $seed",
        (1 .. iterations).map {
            val context = PropertyRunner(context, random)
            val result = runCatching { block(context) }
            context to result
        }.takeUntilFirst { (_, result) ->
            result.isFailure && !continueAfterFirstFailure
        }.map { (context, result) ->
            val name = context.values.joinToString(separator = ", ") { "${it.first.name}: ${it.second}" }
            DynamicTest.dynamicTest(name) {
                result.getOrThrow()
            }
        }
    )
}

internal fun <T> List<T>.takeUntilFirst(
    predicate: (T) -> Boolean
): List<T> = buildList {
    for (element in this@takeUntilFirst) {
        add(element)
        if (predicate(element)) break
    }
}