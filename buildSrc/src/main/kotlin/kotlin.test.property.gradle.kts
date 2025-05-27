import groovy.util.Node
import groovy.util.NodeList
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.net.URI

repositories {
  mavenCentral()
  google()
}

val projectNameWithDots = project.name.replace('-', '.')
project.group = property("projects.group").toString()
project.version = "0.1"

plugins.apply("org.jetbrains.kotlin.multiplatform")
plugins.apply("com.android.library")
plugins.apply("org.jetbrains.dokka")

val doNotPublish = listOf("kotlin-test-property-example")
if (project.name !in doNotPublish)
  plugins.apply("com.vanniktech.maven.publish")

configure<JavaPluginExtension> {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

tasks {
  withType<Test>().configureEach {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    useJUnitPlatform()
  }
}

configure<KotlinMultiplatformExtension> {
  compilerOptions {
    explicitApi()
    freeCompilerArgs.addAll(
      "-Xexpect-actual-classes",
      "-Xcontext-parameters",
      "-Xreport-all-warnings",
      "-Xrender-internal-diagnostic-names",
      "-Wextra",
      "-Xuse-fir-experimental-checkers",
    )
  }

  jvmToolchain(8)

  jvm {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_1_8
    }
    tasks.named<Jar>("jvmJar") {
      manifest {
        attributes["Automatic-Module-Name"] = projectNameWithDots
      }
    }
  }

  js {
    nodejs()
    browser()
  }

  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    nodejs()
    d8()
  }

  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
  wasmWasi {
    nodejs()
  }

  androidTarget()

  // Native: https://kotlinlang.org/docs/native-target-support.html
  // -- Tier 1 --
  linuxX64()
  macosX64()
  macosArm64()
  iosSimulatorArm64()
  iosX64()
  // -- Tier 2 --
  linuxArm64()
  watchosSimulatorArm64()
  watchosX64()
  watchosArm32()
  watchosArm64()
  tvosSimulatorArm64()
  tvosX64()
  tvosArm64()
  iosArm64()
  // -- Tier 3 --
  mingwX64()
  // Android Native and watchOS not included

  applyDefaultHierarchyTemplate()

  sourceSets {
    val androidAndJvmMain by creating { dependsOn(commonMain.get()) }
    val androidAndJvmTest by creating { dependsOn(commonTest.get()) }

    jvmMain.get().dependsOn(androidAndJvmMain)
    androidMain.get().dependsOn(androidAndJvmMain)

    jvmTest.get().dependsOn(androidAndJvmTest)
    androidUnitTest.get().dependsOn(androidAndJvmTest)

    val nonJvmMain by creating { dependsOn(commonMain.get()) }
    val nonJvmTest by creating { dependsOn(commonTest.get()) }

    jsMain.get().dependsOn(nonJvmMain)
    jsTest.get().dependsOn(nonJvmTest)

    val nativeAndWasmMain by creating { dependsOn(nonJvmMain) }
    val nativeAndWasmTest by creating { dependsOn(nonJvmTest) }

    nativeMain.get().dependsOn(nativeAndWasmMain)
    wasmJsMain.get().dependsOn(nativeAndWasmMain)
    wasmWasiMain.get().dependsOn(nativeAndWasmMain)

    nativeTest.get().dependsOn(nativeAndWasmTest)
    wasmJsTest.get().dependsOn(nativeAndWasmTest)
    wasmWasiTest.get().dependsOn(nativeAndWasmTest)
  }
}

configure<com.android.build.gradle.LibraryExtension> {
  namespace = projectNameWithDots
  compileSdk = 35
  defaultConfig {
    minSdk = 21
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
  extensions.findByType<KotlinProjectExtension>()?.sourceSets?.forEach { kotlinSourceSet ->
    dokkaSourceSets.named(kotlinSourceSet.name) {
      if ("androidAndJvm" in kotlinSourceSet.name) {
        platform.set(org.jetbrains.dokka.Platform.jvm)
      }
      perPackageOption {
        matchingRegex.set(".*\\.internal.*")
        suppress.set(true)
      }
      externalDocumentationLink {
        url.set(URI("https://kotlinlang.org/api/kotlinx.serialization/").toURL())
      }
      externalDocumentationLink {
        url.set(URI("https://kotlinlang.org/api/kotlinx.coroutines/").toURL())
      }
      skipDeprecated.set(true)
      reportUndocumented.set(false)
    }
  }
}

afterEvaluate {
  val publications = extensions.findByType(PublishingExtension::class.java)?.publications ?: return@afterEvaluate
  val platformPublication: MavenPublication? = publications.findByName("jvm") as? MavenPublication

  if (platformPublication != null) {
    lateinit var platformXml: XmlProvider
    platformPublication.pom?.withXml { platformXml = this }

    (publications.findByName("kotlinMultiplatform") as? MavenPublication)?.run {
      // replace pom
      pom.withXml {
        val xmlProvider = this
        val root = xmlProvider.asNode()
        // Remove the original content and add the content from the platform POM:
        root.children().toList().forEach { root.remove(it as Node) }
        platformXml.asNode().children().forEach { root.append(it as Node) }

        // Adjust the self artifact ID, as it should match the root module's coordinates:
        ((root.get("artifactId") as NodeList).first() as Node).setValue(artifactId)

        // Set packaging to POM to indicate that there's no artifact:
        root.appendNode("packaging", "pom")

        // Remove the original platform dependencies and add a single dependency on the platform
        // module:
        val dependencies = (root.get("dependencies") as NodeList).first() as Node
        dependencies.children().toList().forEach { dependencies.remove(it as Node) }
        val singleDependency = dependencies.appendNode("dependency")
        singleDependency.appendNode("groupId", platformPublication.groupId)
        singleDependency.appendNode("artifactId", platformPublication.artifactId)
        singleDependency.appendNode("version", platformPublication.version)
        singleDependency.appendNode("scope", "compile")
      }
    }

    tasks
      .matching { it.name == "generatePomFileForKotlinMultiplatformPublication" }
      .configureEach {
        dependsOn(
          "generatePomFileFor${platformPublication.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}Publication"
        )
      }
  }
}
