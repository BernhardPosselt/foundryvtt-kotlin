package at.posselt.datafield

import at.posselt.datafield.jsonschema.types.ArrayDefinition
import at.posselt.datafield.jsonschema.types.BooleanDefinition
import at.posselt.datafield.jsonschema.types.DoubleDefinition
import at.posselt.datafield.jsonschema.types.IntegerDefinition
import at.posselt.datafield.jsonschema.types.MapDefinition
import at.posselt.datafield.jsonschema.types.Reference
import at.posselt.datafield.jsonschema.types.Schema
import at.posselt.datafield.jsonschema.types.StringDefinition
import at.posselt.datafield.jsonschema.types.TypeDefinition
import at.posselt.datafield.jsonschema.types.toClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream


fun parseSchema(schema: InputStream): Schema {
    val json = Json
    return schema.use { json.decodeFromStream<Schema>(schema) }
}


private fun getType(typeDefinition: TypeDefinition): TypeName =
    when (typeDefinition) {
        is IntegerDefinition -> Integer::class.asTypeName()
        is Reference -> typeDefinition.ref.toClassName()
        is StringDefinition -> if (typeDefinition.enum != null) {
            throw RuntimeException("Inline Enums are not supported!")
        } else {
            String::class.asTypeName()
        }

        is ArrayDefinition -> List::class.asClassName().parameterizedBy(
            getType(typeDefinition.items)
        )

        is BooleanDefinition -> Boolean::class.asTypeName()
        is DoubleDefinition -> Double::class.asTypeName()
        is MapDefinition -> {
            Map::class.asClassName().parameterizedBy(
                String::class.asClassName(),
                getType(typeDefinition.additionalProperties)
            )
        }
    }

fun generateType(def: Schema): FileSpec {
    val klass = def.id.toClassName()
    val enum = def.enum
    return FileSpec.builder(klass)
        .indent("    ")
        .addType(
            if (def.type == "string" && enum != null) {
                TypeSpec.enumBuilder(klass.simpleName)
                    .apply { enum.forEach { addEnumConstant(it) } }
                    .build()
            } else {
                TypeSpec.interfaceBuilder(klass.simpleName)
                    .apply {
                        addProperties(def.properties.map { (string, property) ->
                            val isNullable = string !in def.required
                            val type = getType(property)
                            PropertySpec
                                .builder(string, type.copy(nullable = isNullable))
                                .build()
                        })
                    }
                    .build()
            })
        .build()
}

private fun addSpec(
    id: String,
    schema: Schema,
    schemasById: Map<String, Schema>,
    fileSpecsById: MutableMap<String, FileSpec>,
) {
    TODO("Not yet implemented")
}

fun generateCode(schemas: List<Schema>) {
    // TODO: prepopulate foundry internal data models
    val schemasById = schemas.associateBy { it.id }
    val fileSpecsById = mutableMapOf<String, FileSpec>()
    schemasById.forEach { (id, schema) ->
        if (id !in fileSpecsById) {
            addSpec(id, schema, schemasById, fileSpecsById)
        }
    }
}
