package com.foundryvtt.core

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface DataValidationOptions {
    val strict: Boolean?
    val fallback: Boolean?
    val partial: Boolean?
    val dropInvalidEmbedded: Boolean?
}

@JsPlainObject
external interface DocumentConstructionContext {
    val parent: Document
    val strict: Boolean?
    val options: DataValidationOptions?
}

abstract external class DataModel(
    data: Any = definedExternally,
    options: DocumentConstructionContext = definedExternally
) {
    fun toObject(source: Boolean = definedExternally): Any
    fun toJSON(): Any
    fun reset()
}