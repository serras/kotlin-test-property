package com.serranofp.kotlin.test.property

import kotlin.random.Random
import kotlin.test.asserter

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
    val seed = seed ?: Random.nextInt()
    val random = Random(seed)
    repeat(iterations) {
        val context = PropertyRunner(context, random)
        try {
            block(context)
        } catch (e: AssertionError) {
            val message = buildString {
                appendLine(e.message ?: "Property failed")
                if (context.values.isNotEmpty()) {
                    var firstLine = true
                    for ((property, value) in context.values) {
                        if (firstLine) append("where ") else append("      ")
                        appendLine("${property.name}: ${value.toString()}")
                        firstLine = false
                    }
                }
                append("Repeat this test by using seed $seed")
            }
            asserter.fail(message, e)
        }
    }
}