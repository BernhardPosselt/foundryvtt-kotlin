package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("integer")
data class IntegerDefinition(
    val minimum: Int
): SpecificTypeDefinition