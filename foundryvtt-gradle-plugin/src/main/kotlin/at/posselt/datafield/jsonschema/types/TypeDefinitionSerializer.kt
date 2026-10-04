package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class TypeDefinitionSerializer : JsonContentPolymorphicSerializer<TypeDefinition>(TypeDefinition::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<TypeDefinition> {
        return when {
            $$"$ref" in element.jsonObject -> Reference.serializer()
            else -> {
                val type = element.jsonObject["type"]?.jsonPrimitive?.content
                when (type) {
                    "string" -> StringDefinition.serializer()
                    "integer" -> IntegerDefinition.serializer()
                    "number" -> DoubleDefinition.serializer()
                    "array" -> ArrayDefinition.serializer()
                    "object" -> MapDefinition.serializer()
                    else -> throw Exception("Invalid type $type")
                }
            }
        }
    }
}