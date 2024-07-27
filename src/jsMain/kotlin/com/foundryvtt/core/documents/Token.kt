package com.foundryvtt.core.documents

import com.foundryvtt.core.Actor
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import com.foundryvtt.core.abstract.Document
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface BarData {
    val attribute: String?
}


@JsPlainObject
external interface TextureData {
    var src: String
    var tint: String
    var anchorX: Double
    var anchorY: Double
    var alphaThreshold: Double
    var fit: String
    var offsetX: Int
    var offsetY: Int
    var scaleX: Double
    var scaleY: Double
}

@JsPlainObject
external interface LightData {
    val negative: Boolean
    val priority: Int
    var alpha: Boolean
    var angle: Int
}

@JsPlainObject
external interface SightData {
    val enabled: Boolean
    val range: Double
    val angle: Int
    val visionMode: String
    val color: String
    val attenuation: Double
    val brightness: Double
    val saturation: Double
    val contrast: Double
}

@JsPlainObject
external interface DetectionModeData {
    val id: String
    val enabled: Boolean
    val range: Double
}

@JsPlainObject
external interface OccludableData {
    val radius: Double
}

@JsPlainObject
external interface RingColorData {
    val ring: String
    val background: String
}

@JsPlainObject
external interface RingSubjectData {
    val scale: Double
    val texture: String
}

@JsPlainObject
external interface RingData {
    val enabled: Boolean
    val colors: RingColorData
    val effects: Double
    val subject: RingSubjectData
}


// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.Token.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class Token : Document {
    companion object : DocumentStatic<Token>

    override fun delete(operation: DatabaseDeleteOperation): Promise<Token>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<Token>

    var name: String
    var displayName: Int
    var displayBars: Int
    var actor: Actor
    var actorId: String?
    var actorLink: Boolean
    var delta: Any?
    var appendNumber: Boolean
    var prependAdjective: Boolean
    var width: Int
    var height: Int
    var texture: TextureData
    var hexagonalShape: Int
    var x: Int
    var y: Int
    var elevation: Int
    var sort: Int
    var locked: Boolean
    var lockRotation: Boolean
    var alpha: Int
    var hidden: Boolean
    var disposition: Int
    var bar1: BarData
    var bar2: BarData
    var light: LightData
    var sight: SightData
    var detectionModes: Array<DetectionModeData>
    var occludable: OccludableData
    var ring: RingData
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Token.update(data: Token, operation: DatabaseUpdateOperation = jso()): Promise<Token> =
    update(data as AnyObject, operation)