package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Schema(
    @SerialName($$"$schema")
    val schema: String,
    @SerialName($$"$id")
    override val id: String,
    override val properties: Map<String, TypeDefinition>,
    override val required: List<String> = emptyList(),
): ObjectLike