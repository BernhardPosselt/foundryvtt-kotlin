package at.posselt

import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class FoundryVTTExtension @Inject constructor(objects: ObjectFactory, projectLayout: ProjectLayout) {
    val moduleFile: RegularFileProperty = objects.fileProperty()
        .convention(projectLayout.projectDirectory.file("module.json"))
    val githubUser: Property<String> = objects.property(String::class.java)
    val githubRepo: Property<String> = objects.property(String::class.java)
}