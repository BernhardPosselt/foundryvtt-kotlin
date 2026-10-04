package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.Serializable

@Serializable
data class BooleanDefinition(
    val type: String,
): TypeDefinition