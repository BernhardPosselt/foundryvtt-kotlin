package com.foundryvtt.core

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

@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
@JsName("foundry.abstract.Document")
abstract external class Document(
    data: AnyObject = definedExternally,
    options: DocumentConstructionContext = definedExternally
) : DataModel {
    @OptIn(ExperimentalStdlibApi::class)
    @JsExternalInheritorsOnly
    open class DocumentStatic<D : Document> {
        fun create(data: Any, operation: DatabaseGetOperation = definedExternally): Promise<D>

        fun create(data: D, operation: DatabaseGetOperation = definedExternally): Promise<D>

        fun createDocuments(
            data: Array<AnyObject>,
            operation: DatabaseGetOperation = definedExternally
        ): Promise<Array<D>>

        fun createDocuments(
            data: Array<D>,
            operation: DatabaseGetOperation = definedExternally
        ): Promise<Array<D>>

        fun updateDocuments(
            data: Array<AnyObject>,
            operation: DatabaseGetOperation = definedExternally
        ): Promise<Array<D>>

        fun updateDocuments(
            data: Array<D>,
            operation: DatabaseGetOperation = definedExternally
        ): Promise<Array<D>>

        fun deleteDocuments(
            data: Array<D>,
            operation: DatabaseGetOperation = definedExternally
        ): Promise<Array<D>>

        fun deleteDocuments(
            data: Array<AnyObject>,
            operation: DatabaseGetOperation = definedExternally
        ): Promise<Array<D>>

        fun get(id: String, operation: DatabaseGetOperation = definedExternally): Promise<D?>
    }

    companion object : DocumentStatic<Document>

    val id: String?
    val uuid: String
    val isEmbedded: Boolean

    open fun update(
        data: AnyObject,
        operation: DatabaseGetOperation = definedExternally
    ): Promise<Document>

    open fun delete(operation: DatabaseGetOperation = definedExternally): Promise<Document>
    open fun getFlag(scope: String, key: String): Any?
    open fun <T> setFlag(scope: String, key: String, value: T): Promise<T>
    open fun unsetFlag(scope: String, key: String): Promise<Any?>
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Document.update(data: Document, operation: DatabaseGetOperation = jso()): Promise<Document> =
    update(data as AnyObject, operation)