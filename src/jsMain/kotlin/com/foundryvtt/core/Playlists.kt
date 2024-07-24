package com.foundryvtt.core

import kotlin.js.Promise

external class Playlists : WorldCollection<Playlist> {
    val playing: Boolean
    fun initialize(): Promise<Unit>
}