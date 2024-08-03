import at.posselt.kingmaker.plugins.ChangeModuleVersion
import at.posselt.kingmaker.plugins.JsonSchemaValidator
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.plain.objects)
    alias(libs.plugins.versions)
}

group = "at.posselt"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
//    compilerOptions {
//        freeCompilerArgs.add("-XXLanguage:+JsAllowInvalidCharsIdentifiersEscaping")
//    }
    js {
        useEsModules()
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            moduleKind = JsModuleKind.MODULE_ES
            useEsClasses = true
        }
        browser {
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                outputDirectory = file("dist")
            }
            webpackTask {
                mainOutputFileName = "main.js"
            }
            testTask {
                useKarma {
                    useFirefoxHeadless()
                }
            }
        }
        binaries.executable() // create a js file
    }
    sourceSets {
        // enable kotlin test for all modules
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        // define a jsMain module
        val jsMain by getting {
            dependencies {
                implementation(project.dependencies.enforcedPlatform(libs.kotlin.wrappers))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-js")
                implementation(libs.kotlin.plain.objects)
                implementation(libs.kotlinx.html)
                implementation(libs.kotlinx.coroutines.js)
            }
        }
        val jsTest by getting {}
    }
}

tasks {
    getByName<Delete>("clean") {
        delete.add("dist")
    }
    getByName("assemble") {
        finalizedBy("copyOldJs")
    }
    getByName("check") {
        finalizedBy("validateJsonFiles")
    }
}

tasks.register<JsonSchemaValidator>("validateJsonFiles") {
    schema(layout.projectDirectory.file("schemas/recipes.json")) {
        add(layout.projectDirectory.file("src/jsMain/resources/recipes.json"))
    }
}

// TODO: remove once fully migrated
tasks.register<Copy>("copyOldJs") {
    from("oldsrc/dist/main.js") {
        rename(".*", "oldmain.js")
    }
    into("dist/")
}

tasks.register<ChangeModuleVersion>("changeModuleVersion") {
    moduleVersion = project.property("moduleVersion") as String
}

/**
 * Run using ./gradlew package -PmoduleVersion=0.0.1
 */
tasks.register<Zip>("package") {
    dependsOn("clean", "build", "copyOldJs", "changeModuleVersion")
    tasks.named("build").get().mustRunAfter("clean")
    archiveFileName.set("release.zip")
    destinationDirectory.set(layout.buildDirectory)
    from("dist") { into("dist") }
    from("docs") { into("docs") }
    from("img") { into("img") }
    from("packs") { into("packs") }
    from("styles") { into("styles") }
    from("templates") { into("templates") }
    from("CHANGELOG.md")
    from("LICENSE")
    from("OpenGameLicense.md")
    from("README.md")
    from("token-map.json")
    from("module.json")
}