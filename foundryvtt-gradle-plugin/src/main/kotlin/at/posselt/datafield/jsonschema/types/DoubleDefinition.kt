package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.Serializable

@Serializable
data class DoubleDefinition(
    val minimum: Int? = null,
    val type: String,
): TypeDefinition