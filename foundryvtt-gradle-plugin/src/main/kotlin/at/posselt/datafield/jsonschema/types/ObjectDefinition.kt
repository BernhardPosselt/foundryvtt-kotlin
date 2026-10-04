package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface ObjectLike {
    val id: String
    val properties: Map<String, TypeDefinition>
    val required: List<String>
}

@Serializable
@SerialName("object")
data class ObjectDefinition(
    @SerialName($$"$id")
    override val id: String,
    override val properties: Map<String, TypeDefinition>,
    override val required: List<String> = emptyList(),
) : SpecificTypeDefinition, ObjectLike