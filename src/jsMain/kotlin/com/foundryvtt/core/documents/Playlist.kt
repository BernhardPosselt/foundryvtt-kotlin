package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import com.foundryvtt.core.abstract.Document
import com.foundryvtt.core.collections.EmbeddedCollection
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PlayNextOptions {
    val direction: Int?
}

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.Playlist.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class Playlist : Document {
    companion object : DocumentStatic<Playlist>

    override fun delete(operation: DatabaseGetOperation): Promise<Playlist>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<Playlist>

    var name: String
    var description: String
    var sounds: EmbeddedCollection<PlaylistSound>
    var channel: String
    var mode: Int
    var playing: Boolean
    var fade: Int
    var folder: Folder
    var sorting: String
    var seed: Int
    var sort: Int
    val playbackOrder: Array<String>
    val visible: Boolean
    fun playAll(): Promise<Playlist>
    fun playNext(soundId: String, options: PlayNextOptions = definedExternally)
    fun playSound(sound: PlaylistSound): Promise<Playlist>
    fun stopSound(sound: PlaylistSound): Promise<Playlist>
    fun stopAll(): Promise<Playlist>
    fun cycleMode(): Promise<Playlist>
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Playlist.update(data: Playlist, operation: DatabaseGetOperation = jso()): Promise<Playlist> =
    update(data as AnyObject, operation)