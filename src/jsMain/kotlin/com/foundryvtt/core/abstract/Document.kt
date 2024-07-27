@file:JsQualifier("foundry.abstract")

package com.foundryvtt.core.abstract

import com.foundryvtt.core.AnyObject
import kotlin.js.Promise

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

