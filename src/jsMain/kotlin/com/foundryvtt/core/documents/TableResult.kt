package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import com.foundryvtt.core.abstract.Document
import js.objects.jso
import kotlin.js.Promise

@JsName("CONFIG.TableResult.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class TableResult : Document {
    companion object : DocumentStatic<TableResult>

    override fun delete(operation: DatabaseDeleteOperation): Promise<TableResult>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<TableResult>

    val icon: String
    fun getChatText(): String

    var type: String
    var text: String
    var img: String
    var documentCollection: String
    var documentId: String
    var weight: Int
    var range: Array<Int>
    var drawn: Boolean
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun TableResult.update(data: TableResult, operation: DatabaseUpdateOperation = jso()): Promise<TableResult> =
    update(data as AnyObject, operation)