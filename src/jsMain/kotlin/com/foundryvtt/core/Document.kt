package com.foundryvtt.core

import js.objects.Record
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

abstract external class Document(
    data: Any = definedExternally,
    options: DocumentConstructionContext = definedExternally
) : DataModel {
    @OptIn(ExperimentalStdlibApi::class)
    @JsExternalInheritorsOnly
    open class DocumentStatic<D> {
        fun create(data: D, operation: DatabaseGetOperation = definedExternally): Promise<ChatMessage>

        fun get(id: String, operation: DatabaseGetOperation = definedExternally): Promise<ChatMessage?>
    }

    companion object : DocumentStatic<Any>

    val id: String?
    val uuid: String
    val isEmbedded: Boolean

    fun update(
        data: Record<String, Any> = definedExternally,
        operation: DatabaseGetOperation = definedExternally
    ): Promise<Document>

    fun delete(operation: DatabaseGetOperation = definedExternally): Promise<Document>
    fun getFlag(scope: String, key: String): Any?
    fun setFlag(scope: String, key: String, value: Any?): Promise<Document>
    fun unsetFlag(scope: String, key: String): Promise<Document>
}