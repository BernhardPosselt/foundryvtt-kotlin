import org.jetbrains.kotlin.gradle.dsl.JsModuleKind

/**
 * Usage:
 *
 * Build project:
 * ./gradlew jsBrowserProductionWebpack
 * Run local dev server:
 * ./gradlew jsBrowserDevelopmentWebpack -t
 */

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
    js {
        useEsModules()
        compilerOptions {
            moduleKind = JsModuleKind.MODULE_ES
            useEsClasses = true
        }
        browser {
            // run tests tasks using karma in chrome and firefox
            distribution {
                outputDirectory = file("dist")
            }
            webpackTask {
                output.libraryTarget = "commonjs2"
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
}

