package at.posselt.datafield

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

enum class Type {
    @SerialName("integer")
    INTEGER,

    @SerialName("number")
    NUMBER,

    @SerialName("string")
    STRING,

    @SerialName("object")
    OBJECT;
}

@Serializable
data class Property(
    val type: Type
)

@Serializable
data class Schema(
    @SerialName($$"$id")
    val id: String,
    @SerialName($$"$schema")
    val schema: String,
    val type: Type,
    val properties: Map<String, Property>,
    val required: List<String> = emptyList(),
)

fun parseSchema(schema: InputStream): Schema {
    val json = Json
    return schema.use { json.decodeFromStream<Schema>(schema) }
}

fun generateCode(schema: Schema): FileSpec {
    val packageName = schema.id.substringBeforeLast('.')
    val className = schema.id.substringAfterLast('.')
    return FileSpec.builder(packageName, className)
        .indent("    ")
        .addType(TypeSpec.interfaceBuilder(className)
            .apply {
                addProperties(schema.properties.map { (string, property) ->
                    val type = when(property.type) {
                        Type.INTEGER -> Integer::class
                        Type.NUMBER -> Double::class
                        Type.STRING -> String::class
                        Type.OBJECT -> TODO()
                    }
                    PropertySpec.builder(string, type)
                        .build()
                })
            }
            .build())
        .build()
}