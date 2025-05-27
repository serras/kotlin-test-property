@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

plugins {
    id("kotlin.test.property")
    alias(libs.plugins.kotlin.powerAssert)
}

kotlin {
    sourceSets {
        commonTest {
            dependencies {
                implementation(projects.kotlinTestPropertyCore)
            }
        }
    }
}

powerAssert {
    functions = listOf("kotlin.assert", "kotlin.test.assertEquals", "kotlin.test.assertTrue", "kotlin.test.assertNull", "kotlin.require", "org.example.AssertScope.assert")
}