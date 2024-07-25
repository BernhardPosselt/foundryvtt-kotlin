package com.foundryvtt.core.documents

import com.foundryvtt.core.AudioContext
import com.foundryvtt.core.Sound
import com.foundryvtt.core.abstract.Document
import kotlin.js.Promise

@JsName("CONFIG.PlaylistSound.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
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