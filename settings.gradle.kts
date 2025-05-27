enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "kotlin-test-property"

include("kotlin-test-property-core")
project(":kotlin-test-property-core").projectDir = file("core")

include("kotlin-test-property-example")
project(":kotlin-test-property-example").projectDir = file("example")