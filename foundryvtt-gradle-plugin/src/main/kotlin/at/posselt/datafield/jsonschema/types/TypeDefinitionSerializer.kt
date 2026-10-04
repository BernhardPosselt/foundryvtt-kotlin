package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object TypeDefinitionSerializer : JsonContentPolymorphicSerializer<TypeDefinition>(TypeDefinition::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        $$"$ref" in element.jsonObject -> Reference.serializer()
        else -> SpecificTypeDefinition.serializer()
    }
}