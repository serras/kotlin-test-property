package com.serranofp.kotlin.test.property

import kotlin.jvm.JvmInline
import kotlin.random.Random
import kotlin.reflect.KProperty

public class PropertyRunner internal constructor(
    context: GeneratorContext,
    internal val random: Random
): GeneratorContext by context {
    internal val values: ArrayDeque<Pair<KProperty<*>, Any?>> = ArrayDeque()

    @JvmInline
    public value class GeneratedValue<T>(public val value: T) {
        public operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    }

    public operator fun <T> Generator<T>.provideDelegate(
        thisRef: Any?, property: KProperty<*>
    ): GeneratedValue<T> {
        val value = this.next(random)
        values.addLast(property to value)
        return GeneratedValue(value)
    }
}

public fun propertyTest(
    iterations: Int = 100,
    context: GeneratorContext = GeneratorContext.Default,
    seed: Int? = null,
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
                appendLine(e.message ?: "Assertion failed")
                if (context.values.isNotEmpty()) {
                    // appendLine("Values for failure:")
                    for ((property, value) in context.values) {
                        appendLine("- ${property.name} = ${value.toString()}")
                    }
                }
                append("To repeat this run, use seed $seed")
            }
            throw AssertionError(message)
        }
    }
}