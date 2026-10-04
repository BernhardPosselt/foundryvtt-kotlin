package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Schema(
    @SerialName($$"$schema")
    val schema: String,
    @SerialName($$"$id")
    val id: String,
    val properties: Map<String, TypeDefinition>,
    val required: Set<String> = emptySet(),
    val type: String,
    val enum: List<String>?
)