plugins {
    id("kotlin.test.property")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(kotlin("test"))
            }
        }
        androidAndJvmMain {
            dependencies {
                api(kotlin("test-junit5"))
                implementation(libs.rgxgen)
            }
        }
        jsMain {
            dependencies {
                api(kotlin("test-js"))
            }
        }
    }
}