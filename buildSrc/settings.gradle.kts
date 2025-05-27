enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "kotlin-test-property-convention"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}
