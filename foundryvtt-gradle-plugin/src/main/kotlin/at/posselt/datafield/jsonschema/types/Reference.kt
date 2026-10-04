package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reference(
    @SerialName($$"$ref")
    val ref: String,
): TypeDefinition