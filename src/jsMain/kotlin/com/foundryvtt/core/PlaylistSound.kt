package com.foundryvtt.core

import kotlin.js.Promise

open external class PlaylistSound : Document {
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