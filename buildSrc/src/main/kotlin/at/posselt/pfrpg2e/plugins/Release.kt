package at.posselt.pfrpg2e.plugins

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.lang.IllegalStateException

abstract class Release : DefaultTask() {
    @get:InputFile
    abstract val releaseZip: RegularFileProperty

    @get:Input
    abstract val version: Property<String>

    @TaskAction
    fun action() {
        val zipFile = releaseZip.asFile.orNull
        if (zipFile == null || !zipFile.exists()) {
            throw IllegalStateException("Need an archive file")
        }
        ProcessBuilder(listOf("git add "))
        println(version.get())
        println(zipFile.absolutePath)
    }
}