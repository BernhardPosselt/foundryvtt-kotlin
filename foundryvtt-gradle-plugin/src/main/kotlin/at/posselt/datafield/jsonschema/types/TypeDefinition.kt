package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable(with = TypeDefinitionSerializer::class)
sealed interface TypeDefinition

