package com.foundryvtt.core.abstract

import com.foundryvtt.core.AnyObject
import js.objects.Record
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise


typealias DatabaseAction = String

sealed external interface DatabaseOperation

@JsPlainObject
external interface DatabaseGetOperation : DatabaseOperation {
    val query: Record<String, Any>?
    val broadcast: Boolean?
    val index: Boolean?
    val indexFields: Array<String>?
    val pack: String?
    val parent: Document
    val parentUuid: String?
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Document.update(data: Document, operation: DatabaseGetOperation = jso()): Promise<Document> =
    update(data as AnyObject, operation)