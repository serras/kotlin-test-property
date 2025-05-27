package com.serranofp.kotlin.test.property

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
