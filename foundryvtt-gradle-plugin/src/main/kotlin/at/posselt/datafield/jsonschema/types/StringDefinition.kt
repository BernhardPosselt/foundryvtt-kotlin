package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StringDefinition(
    val minimum: Int? = null,
    val enum: List<String>? = null,
    val type: String,
): TypeDefinition