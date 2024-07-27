package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface ShapeData

@JsName("CONFIG.Drawing.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class DrawingDocument : ClientDocument {
    companion object : DocumentStatic<DrawingDocument>

    override fun delete(operation: DatabaseDeleteOperation): Promise<DrawingDocument>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<DrawingDocument>

    val isAuthor: Boolean

    var _id: String
    var author: User?
    var shape: ShapeData
    var x: Int
    var y: Int
    var elevation: Int
    var sort: Int
    var rotation: Int
    var bezierFactor: Double
    var fillType: Int
    var fillColor: Int
    var fillAlpha: Double
    var strokeWidth: Int
    var strokeColor: String
    var texture: TextureData?
    var text: String
    var fontFamily: String
    var fontSize: Int
    var textColor: String
    var textAlpha: Double
    var hidden: Boolean
    var locked: Boolean
    var `interface`: Boolean
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun DrawingDocument.update(
    data: DrawingDocument,
    operation: DatabaseUpdateOperation = jso()
): Promise<DrawingDocument> =
    update(data as AnyObject, operation)