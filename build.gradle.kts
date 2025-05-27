plugins {
    base
    id(libs.plugins.kotlin.multiplatform.get().pluginId) apply false
    id(libs.plugins.android.library.get().pluginId) apply false
    alias(libs.plugins.publish) apply false
    id(libs.plugins.dokka.get().pluginId)
    alias(libs.plugins.kotlin.binaryCompatibilityValidator)
}

dependencies {
    dokka(projects.kotlinTestPropertyCore)
}

dokka {
    moduleName.set("kotlin-test-property")
    dokkaPublications.html {
        outputDirectory.set(layout.projectDirectory.dir("docs"))
    }
}

apiValidation {
    ignoredProjects.addAll(listOf("kotlin-test-property-example"))
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib.enabled = true
}