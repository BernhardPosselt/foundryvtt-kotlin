package com.foundryvtt.core

import js.objects.Record
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface DatabaseGetOperation<D> {
    val query: Record<String, Any>?
    val broadcast: Boolean?
    val index: Boolean?
    val indexFields: Array<String>?
    val pack: String?
    val parent: Document<in D>?
    val parentUuid: String?
}

abstract external class Document<D> : DataModel<D> {
    val id: String?
    val uuid: String
    val isEmbedded: Boolean
    val system: D

    fun update(
        data: Record<String, Any> = definedExternally,
        operation: DatabaseGetOperation<D> = definedExternally
    ): Promise<Document<D>>

    fun delete(operation: DatabaseGetOperation<D> = definedExternally): Promise<Document<D>>
    fun getFlag(scope: String, key: String): Any?
    fun setFlag(scope: String, key: String, value: Any?): Promise<Document<D>>
    fun unsetFlag(scope: String, key: String): Promise<Document<D>>
}