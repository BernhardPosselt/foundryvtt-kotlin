package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.Serializable

@Serializable
data class MapDefinition(
    val type: String,
    val additionalProperties: TypeDefinition
): TypeDefinition