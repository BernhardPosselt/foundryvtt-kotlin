package at.posselt.datafield.jsonschema.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
@Serializer(TypeDefinitionSerializer::class)
sealed interface TypeDefinition

