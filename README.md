# Property Testing for `kotlin.test`

`kotlin-test-property` tries to bring property-based testing to `kotlin.test`
with minimal dependencies and minimal machinery. In particular, you do not
need to install any additional plug-in to run tests from your IDE.

**Property testing** builds on the idea that we should talk about properties
we expect of functions instead of particular runs, as unit tests do.
Still, we need to obtain concrete values to check our properties: this is
the task of **generators**, which define the shape of values expected by
the functions under test.

## Writing a test

Tests using `kotlin-test-property` almost always look as follows.
It is important to use _both_ `@Test @PropertyTest` annotations for the test
to be correctly detected and run by the IDE.

```kotlin
@Test @PropertyTest
fun test() = propertyTest {
    // generate values
    val x by int()
    val y by list(string())
    // assert properties of values
    assertTrue(x > 0)
}
```

We strongly recommend using [Power-assert](https://kotlinlang.org/docs/power-assert.html)
to get nicer reporting once a property fails.

## Describing generation

`kotlin-test-property` provides a handful of basic generators for the most
common types, like `int()` or `string()`. In case of generic types, you must
provide generators for each type argument; for example `map(int(), string())`
generates maps of integers to strings.

To generate instances of your own types, the easiest way is to combine
several generators using the constructor. The first option showcased below
is often the shortest, but sometimes leads to problems with inference if
the class to construct is generic.

```kotlin
data class Example(val number: Int, val strings: List<String>)

// option 1
val example by ::Example.by(int(), list(string()))
// option 2
val example by combine(int(), list(string())) { n, s -> Example(n, s) }
```

If you have a hierarchy of classes, you can use `or` or `choice` to
describe the different variations. The second function allows you to
describe different probabilities for each case.

```kotlin
sealed interface Example {
    data class Number(val number: Int): Example
    data class Thing(val string: String): Example
}

// option 1
val example by ::Number.by(int()) or ::Thing.by(string())
// option 2
val example by choice(
    2 to ::Number.by(int()),
    3 to ::Thing.by(string())
)
```
