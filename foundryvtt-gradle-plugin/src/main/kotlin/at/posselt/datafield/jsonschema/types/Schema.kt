package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Schema(
    @SerialName($$"$schema")
    val schema: String,
    val definition: TypeDefinition
)