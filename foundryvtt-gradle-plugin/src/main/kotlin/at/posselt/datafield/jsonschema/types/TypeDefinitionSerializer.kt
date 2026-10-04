package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject

class TypeDefinitionSerializer : KSerializer<TypeDefinition> {
    private val polymorphic = TypeDefinition.serializer()
    private val refSerializer = TypeDefinition.serializer()
    override val descriptor = SerialDescriptor(
        "at.posselt.datafield.jsonschema.types.TypeDefinition", polymorphic.descriptor
    )

    override fun serialize(encoder: Encoder, value: TypeDefinition) {
        if (value is Reference) {
            refSerializer.serialize(encoder, value)
        } else {
            polymorphic.serialize(encoder, value)
        }
    }

    override fun deserialize(decoder: Decoder): TypeDefinition {
        val input = decoder as? JsonDecoder ?: throw RuntimeException("Only JSON is supported")
        val element = input.decodeJsonElement()
        return if (element is JsonObject) {
            if (element.containsKey($$"$ref")) {
                input.json.decodeFromJsonElement(polymorphic, element)
            } else {
                input.json.decodeFromJsonElement(refSerializer, element)
            }
        } else {
            throw Error("Tried to deserialize non object")
        }
    }
}