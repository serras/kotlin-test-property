package com.serranofp.kotlin.test.property

context(_: GeneratorContext)
public fun boolean(): Generator<Boolean> =
    Generator { random -> random.nextBoolean() }

context(ctx: GeneratorContext)
public fun int(min: Int = 0, max: Int = ctx.size): Generator<Int> =
    Generator { random -> random.nextInt(min, max) }

context(_: GeneratorContext)
public fun int(range: IntRange): Generator<Int> = int(range.start, range.endInclusive)

context(ctx: GeneratorContext)
public fun long(min: Long = 0, max: Long = ctx.size.toLong()): Generator<Long> =
    Generator { random -> random.nextLong(min, max) }

context(_: GeneratorContext)
public fun long(range: LongRange): Generator<Long> = long(range.start, range.endInclusive)

context(_: GeneratorContext)
public fun char(range: CharRange = ' ' .. '~'): Generator<Char> = from(range)

context(ctx: GeneratorContext)
public fun string(chars: CharRange = ' ' .. '~', size: Int = ctx.size): Generator<String> =
    array(char(chars), size).map { it.joinToString() }
