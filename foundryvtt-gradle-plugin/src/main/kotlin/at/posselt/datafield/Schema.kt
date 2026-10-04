package at.posselt.datafield

import at.posselt.datafield.jsonschema.types.IntegerDefinition
import at.posselt.datafield.jsonschema.types.ObjectDefinition
import at.posselt.datafield.jsonschema.types.Reference
import at.posselt.datafield.jsonschema.types.Schema
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream


fun parseSchema(schema: InputStream): Schema {
    val json = Json
    return schema.use { json.decodeFromStream<Schema>(schema) }
}

fun generateInterface(def: ObjectDefinition): FileSpec {
    val packageName = def.id.substringBeforeLast('.')
    val className = def.id.substringAfterLast('.')
    return FileSpec.builder(packageName, className)
        .indent("    ")
        .addType(TypeSpec.interfaceBuilder(className)
            .apply {
                addProperties(def.properties.map { (string, property) ->
                    val type = when(property) {
                        is IntegerDefinition -> Integer::class
                        is ObjectDefinition -> TODO()
                        is Reference -> TODO()
                    }
                    PropertySpec.builder(string, type)
                        .build()
                })
            }
            .build())
        .build()
}

fun generateCode(schema: Schema): FileSpec {

}