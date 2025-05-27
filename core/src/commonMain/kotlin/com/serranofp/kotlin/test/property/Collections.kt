package com.serranofp.kotlin.test.property

import kotlin.enums.enumEntries

context(ctx: GeneratorContext)
public inline fun <reified A> array(element: Generator<A>, size: Int = ctx.size): Generator<Array<A>> =
    Generator { random -> Array(random.nextInt(size)) { element.next(random) } }

context(ctx: GeneratorContext)
public fun <A> list(element: Generator<A>, size: Int = ctx.size): Generator<List<A>> = Generator { random ->
    val size = random.nextInt(size)
    (0 .. size).map { element.next(random) }
}

context(ctx: GeneratorContext)
public fun <A> set(element: Generator<A>, size: Int = ctx.size): Generator<Set<A>> =
    list(element, size).map { it.toSet() }

context(_: GeneratorContext)
public fun <A, B> pair(first: Generator<A>, second: Generator<B>): Generator<Pair<A, B>> =
    Generator { random -> first.next(random) to second.next(random) }

context(_: GeneratorContext)
public fun <A, B, C> triple(first: Generator<A>, second: Generator<B>, third: Generator<C>): Generator<Triple<A, B, C>> =
    Generator { random -> Triple(first.next(random), second.next(random), third.next(random)) }

context(_: GeneratorContext)
public fun <K, V> map(key: Generator<K>, value: Generator<V>): Generator<Map<K, V>> =
    list(pair(key, value)).map { it.toMap() }

context(_: GeneratorContext)
public inline fun <reified E: Enum<E>> enum(): Generator<E> =
    from(enumEntries<E>())
