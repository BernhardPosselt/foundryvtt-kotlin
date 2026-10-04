import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask


plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plain.objects)
    alias(libs.plugins.versions)
}

group = "at.posselt"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
    js {
        useEsModules()
        compilerOptions {
            target = "es2015"
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.io.encoding.ExperimentalEncodingApi",
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=kotlin.contracts.ExperimentalContracts",
                "-opt-in=kotlin.ExperimentalStdlibApi",
                "-opt-in=kotlin.js.ExperimentalJsExport",
                "-opt-in=kotlin.js.ExperimentalWasmJsInterop",
                "-opt-in=kotlin.js.ExperimentalJsStatic",
                "-opt-in=kotlin.time.ExperimentalTime",
                "-Xreturn-value-checker=full"
            )
        }
        browser {
            testTask {
                useKarma {
                    useFirefoxHeadless()
                }
            }
        }
    }
    sourceSets {
        // define a jsMain module
        jsMain {
            dependencies {
                implementation(project.dependencies.enforcedPlatform(libs.kotlin.wrappers))
                implementation(libs.kotlin.wrappers.js)
                implementation(libs.kotlin.wrappers.web)
                implementation(libs.kotlin.plain.objects)
                implementation(libs.kotlinx.html)
                implementation(libs.kotlinx.coroutines.js)
                implementation(libs.jsonschemavalidator.js)
            }
        }
        jsTest {
            dependencies {
                implementation(libs.kotlin.test.js)
            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/BernhardPosselt/foundryvtt")
            credentials {
                username = "bernhardposselt"
                password = System.getenv("GITHUB_PACKAGES_TOKEN")
            }
        }
    }
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    val qualifiers = listOf("preview", "alpha", "beta", "m", "cr", "rc") // order is important
    fun maturityLevel(version: String): Int {
        val index = qualifiers.indexOfFirst {
            version.matches(".*[.\\-]$it[.\\-\\d]*".toRegex(RegexOption.IGNORE_CASE))
        }
        return if (index < 0) qualifiers.size else index
    }

    rejectVersionIf {
        maturityLevel(candidate.version) < maturityLevel(currentVersion)
    }
}