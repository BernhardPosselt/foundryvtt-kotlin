package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface TileRestrictions {
    val light: Boolean
    val weather: Boolean
}

@JsPlainObject
external interface TileOcclusion {
    val mode: Int
    val alpha: Double
}

@JsPlainObject
external interface TileVideo {
    val loop: Boolean
    val autoplay: Boolean
    val alpha: Double
}

@JsName("CONFIG.Tile.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class TileDocument : ClientDocument {
    companion object : DocumentStatic<TileDocument>

    override fun delete(operation: DatabaseDeleteOperation): Promise<TileDocument>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<TileDocument>

    var _id: String
    var texture: TextureData
    var width: Int
    var height: Int
    var x: Int
    var y: Int
    var elevation: Int
    var sort: Int
    var rotation: Int
    var alpha: Double
    var hidden: Boolean
    var locked: Boolean
    var restrictions: TileRestrictions
    var occlusion: TileOcclusion
    var video: TileVideo
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun TileDocument.update(data: TileDocument, operation: DatabaseUpdateOperation = jso()): Promise<TileDocument> =
    update(data as AnyObject, operation)