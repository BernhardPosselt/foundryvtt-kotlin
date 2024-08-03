package at.posselt.kingmaker.plugins

import io.github.optimumcode.json.schema.JsonSchema
import io.github.optimumcode.json.schema.ValidationError
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.TaskAction

data class SchemaSource(
    val schemaFile: RegularFile,
    val jsonFilesToValidate: MutableList<RegularFile> = mutableListOf(),
)

abstract class JsonSchemaValidator : DefaultTask() {
    private val schemaList: MutableList<SchemaSource> = mutableListOf()

    @TaskAction
    fun action() {
        schemaList.forEach { schema ->
            val schemaFile = schema.schemaFile.asFile
            val validator = JsonSchema.fromDefinition(schemaFile.readText())
            schema.jsonFilesToValidate.forEach { file ->
                val jsonFile = file.asFile
                println("Validating JSON file ${jsonFile.absolutePath} using schema ${schemaFile.absoluteFile}")
                val json = parseToJsonElement(jsonFile.readText())
                val errorCollector = mutableListOf<ValidationError>()
                validator.validate(json, errorCollector::add)
                if (errorCollector.isNotEmpty()) {
                    val msg = "Failed to validate ${jsonFile.absolutePath} using schema ${schemaFile.absoluteFile}: " +
                            errorCollector.joinToString("\n") { it.message }
                    throw GradleException(msg)
                }
            }
        }
    }

    fun schema(file: RegularFile, files: MutableList<RegularFile>.() -> Unit) {
        val schema = SchemaSource(schemaFile = file)
        schema.jsonFilesToValidate.files()
        schemaList.add(schema)
    }
}

