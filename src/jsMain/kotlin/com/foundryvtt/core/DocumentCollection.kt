package com.foundryvtt.core

import js.collections.JsSet
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface FieldFilter

@JsPlainObject
external interface SearchOptions {
    val query: String?
    val filters: Array<FieldFilter>?
    val exclude: Array<String>
}

open external class DocumentCollection<T : Document>(
    data: Array<AnyObject>
) : Collection<T> {
    val _source: Array<AnyObject>
    val apps: Array<ApplicationV2>
    val documentName: String
    val documentClass: JsClass<Document>
    val invalidDocumentIds: JsSet<String>
    val name: String

    fun search(options: SearchOptions = definedExternally): Array<String>
}