package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.Serializable

@Serializable
data class ArrayDefinition(
    val type: String,
    val items: TypeDefinition
): TypeDefinition