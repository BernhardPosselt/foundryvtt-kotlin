package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("object")
data class ObjectDefinition(
    @SerialName($$"$id")
    val id: String,
    val properties: Map<String, TypeDefinition>,
    val required: List<String> = emptyList(),
): TypeDefinition