package at.posselt.pfrpg2e.plugins

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.lang.IllegalStateException
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*
import kotlinx.serialization.Serializable

@Serializable
private data class GetRelaseResponse(
    val id: String,
)

@Serializable
private data class GetRelase(
    val tag_name: String,
    val target_commitish: String = "master",
    val name: String,
    val body: String = "",
    val draft: Boolean = false,
    val prerelease: Boolean = false,
    val generate_release_notes: Boolean = false,
)

@Serializable
private data class FoundryReleaseVersion(
    val id: String,
    val dryRun: Boolean = false,
    val release: FoundryRelease,
) {
    @Serializable
    data class FoundryRelease(
        val version: String,
        val manifest: String,
        val notes: String,
        val compatibility: FoundryCompatibility,
    )

    @Serializable
    data class FoundryCompatibility(
        val minimum: String,
        val verified: String,
        val maximum: String,
    )
}

abstract class ReleaseModule : DefaultTask() {
    @get:InputFile
    abstract val releaseZip: RegularFileProperty

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val foundryVersion: Property<String>

    @get:Input
    abstract val githubRepo: Property<String>

    @TaskAction
    fun action() {
        val githubToken = System.getenv("GITHUB_TOKEN") ?: throw IllegalStateException("GITHUB_TOKEN not set")
        val foundryToken = System.getenv("FOUNDRY_TOKEN") ?: throw IllegalStateException("FOUNDRY_TOKEN not set")
        val releaseVersion = version.get()
        val targetFoundryVersion = foundryVersion.get()
        val repo = githubRepo.get()
        val archive = releaseZip.asFile.orNull
        if (archive == null || !archive.exists()) {
            throw IllegalStateException("Need an archive file")
        }
        exec(listOf("git", "add", "build.gradle.kts", "module.json"))
        exec(listOf("git", "commit", "-m", "release $releaseVersion"))
        exec(listOf("git", "push", "origin", "master"))
        exec(listOf("git", "tag", "$releaseVersion"))
        exec(listOf("git", "push", "--tags"))
        println(archive.absolutePath)

        val client = HttpClient() {
            expectSuccess = true
            install(ContentNegotiation) {
                json()
            }
        }
        runBlocking {
            val baseUrl = "https://api.github.com/repos/$repo"
            val releaseId: String = client.post("$baseUrl/releases") {
                accept(ContentType.Application.Json)
                bearerAuth(githubToken)
                setBody(GetRelase(tag_name = releaseVersion, name = releaseVersion))
            }.body<GetRelaseResponse>().id
            client.post("$baseUrl/releases/$releaseId/assets?name=release.zip") {
                accept(ContentType.Application.Json)
                bearerAuth(githubToken)
                setBody(archive.readChannel())
            }
            client.post("https://api.foundryvtt.com/_api/packages/release_version/") {
                accept(ContentType.Application.Json)
                bearerAuth(foundryToken)
                setBody(
                    FoundryReleaseVersion(
                        id = releaseVersion,
                        dryRun = true,
                        release = FoundryReleaseVersion.FoundryRelease(
                            version = releaseVersion,
                            manifest = "https://raw.githubusercontent.com/$repo/$releaseVersion/module.json",
                            notes = "https://github.com/$repo/blob/master/CHANGELOG.md",
                            compatibility = FoundryReleaseVersion.FoundryCompatibility(
                                minimum = targetFoundryVersion,
                                verified = targetFoundryVersion,
                                maximum = targetFoundryVersion,
                            )
                        ),
                    )
                )
            }
            client.close()
        }
    }

    private fun exec(commands: List<String>) {
        ProcessBuilder(commands)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .directory(project.projectDir)
            .start()
            .waitFor()
    }
}