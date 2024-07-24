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

@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
@JsName("foundry.abstract.DataModel")
abstract external class DataModel(
    data: AnyObject = definedExternally,
    options: DocumentConstructionContext = definedExternally
) {
    open fun toObject(source: Boolean = definedExternally): AnyObject
    open fun toJSON(): AnyObject
    open fun reset()
}