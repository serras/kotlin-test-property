package com.serranofp.kotlin.test.property

context(_: GeneratorContext)
public fun <T> constant(value: T): Generator<T> = Generator { value }

context(_: GeneratorContext)
public fun <T> Generator<T>.orNull(
    weight: Int = 10
): Generator<T?> = choose(
    1 to constant(null),
    weight to this
)

context(_: GeneratorContext)
public fun <T> from(collection: Iterable<T>): Generator<T> = from(collection.toList())

context(_: GeneratorContext)
public fun <T> from(collection: Collection<T>): Generator<T> = Generator { collection.random(it) }

context(_: GeneratorContext)
public fun <T> choice(one: Generator<T>, vararg more: Generator<T>): Generator<T> {
    val size = 1 + more.size
    return Generator { random ->
        when (val i = random.nextInt(size)) {
            0 -> one.next(random)
            else -> more[i].next(random)
        }
    }
}

context(_: GeneratorContext)
public fun <T> choose(
    one: Pair<Int, Generator<T>>,
    vararg more: Pair<Int, Generator<T>>
): Generator<T> {
    val (firstWeight, firstValue) = one
    require(firstWeight > 0) { "All weights should be greater than 0" }
    require(more.all { (weight, _) -> weight > 0 }) { "All weights should be greater than 0" }

    val size = firstWeight + more.sumOf { (weight, _) -> weight }

    return Generator { random ->
        val choice = random.nextInt(size)

        var accumulated = firstWeight
        if (choice < accumulated) {
            return@Generator firstValue.next(random)
        }

        for ((weight, value) in more) {
            accumulated += weight
            if (choice < accumulated) {
                return@Generator value.next(random)
            }
        }

        throw IllegalStateException("You should never reach this point")
    }
}

context(ctx: GeneratorContext)
public fun <A> Generator<A>.filter(predicate: (A) -> Boolean): Generator<A> = Generator { random ->
    var attempts = 0
    while (attempts < ctx.size) {
        val result = this@filter.next(random)
        if (predicate(result)) return@Generator result
        attempts++
    }
    throw IllegalStateException("Too many attempts while generating random values")
}

context(ctx: GeneratorContext)
public fun <A, B> ((A) -> B).by(
    first: Generator<A>
): Generator<B> = Generator { random ->
    this@by(first.next(random))
}

context(ctx: GeneratorContext)
public fun <A, B> Generator<A>.map(
    transform: (A) -> B
): Generator<B> = Generator { random ->
    transform(next(random))
}

context(ctx: GeneratorContext)
public fun <A, B, C> ((A, B) -> C).by(
    first: Generator<A>,
    second: Generator<B>
): Generator<C> = Generator { random ->
    this@by(first.next(random), second.next(random))
}

context(ctx: GeneratorContext)
public fun <A, B, C, D> ((A, B, C) -> D).by(
    first: Generator<A>,
    second: Generator<B>,
    third: Generator<C>
): Generator<D> = Generator { random ->
    this@by(first.next(random), second.next(random), third.next(random))
}
