package com.serranofp.kotlin.test.property

import kotlin.jvm.JvmInline
import kotlin.random.Random
import kotlin.reflect.KProperty

@Target(AnnotationTarget.FUNCTION)
public expect annotation class PropertyTest()

public expect class PropertyTestResult

public expect fun propertyTest(
    iterations: Int = 100,
    context: GeneratorContext = GeneratorContext.Default,
    seed: Int? = null,
    continueAfterFirstFailure: Boolean = false,
    block: PropertyRunner.() -> Unit
): PropertyTestResult

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