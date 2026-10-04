package at.posselt.datafield.jsonschema.types

import com.squareup.kotlinpoet.ClassName

fun String.toClassName(): ClassName = ClassName(
    substringBeforeLast('.'),
    substringAfterLast('.')
)