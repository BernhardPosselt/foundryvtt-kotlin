import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.versions)
}

group = "at.posselt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
        )
    }
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.java)
    implementation(libs.ktor.client.negotiation)
    implementation(libs.ktor.client.json)
    implementation(libs.jsonschemavalidator)
    implementation(libs.kotlinpoet)
}

gradlePlugin {
    plugins {
        create("foundryvtt") {
            id = "at.posselt.foundryvtt"
            implementationClass = "at.posselt.FoundryVTT"
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