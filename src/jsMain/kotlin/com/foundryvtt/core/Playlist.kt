package com.foundryvtt.core

import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface PlayNextOptions {
    val direction: Int?
}

external class Playlist : Document {
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