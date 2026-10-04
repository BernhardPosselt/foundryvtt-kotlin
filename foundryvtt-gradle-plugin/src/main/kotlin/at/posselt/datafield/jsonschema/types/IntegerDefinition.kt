package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.Serializable

@Serializable
data class IntegerDefinition(
    val minimum: Int
): TypeDefinition