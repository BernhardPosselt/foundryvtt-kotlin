package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import com.foundryvtt.core.abstract.Document
import com.foundryvtt.core.abstract.DocumentConstructionContext
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface TextAnchorData {
    val scale: Double?
    val duration: Int?
}

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.Note.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class NoteDocument(
    data: AnyObject = definedExternally,
    options: DocumentConstructionContext = definedExternally
) : Document {
    companion object : DocumentStatic<NoteDocument>;

    override fun delete(operation: DatabaseDeleteOperation): Promise<NoteDocument>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<NoteDocument>

    var _id: String
    var entryId: String
    var pageId: String
    var x: Int
    var y: Int
    var elevation: Int
    var sort: Int
    var textureData: TextureData
    var iconSize: Int
    var text: String
    var fontFamily: String
    var fontSize: Int
    var textAnchor: Int
    var textColor: String
    var global: Boolean
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun NoteDocument.update(data: NoteDocument, operation: DatabaseUpdateOperation = jso()): Promise<NoteDocument> =
    update(data as AnyObject, operation)