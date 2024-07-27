package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.AudioContext
import com.foundryvtt.core.Sound
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import com.foundryvtt.core.abstract.Document
import js.objects.jso
import kotlin.js.Promise

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.PlaylistSound.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
open external class PlaylistSound : Document {
    companion object : DocumentStatic<PlaylistSound>

    override fun delete(operation: DatabaseDeleteOperation): Promise<PlaylistSound>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<PlaylistSound>

    val sound: Sound?
    val fadeDuration: Int
    val context: AudioContext
    val effectiveVolume: Int
    var name: String
    var description: String
    var path: String
    var channel: String
    var playing: Boolean
    var pausedTime: Int
    var repeat: Boolean
    var volume: Int
    var fade: Int
    var sort: Int

    fun sync()
    fun load(): Promise<Unit>
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun PlaylistSound.update(data: PlaylistSound, operation: DatabaseUpdateOperation = jso()): Promise<PlaylistSound> =
    update(data as AnyObject, operation)