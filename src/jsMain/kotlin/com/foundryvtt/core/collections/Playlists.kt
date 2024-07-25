package com.foundryvtt.core.collections

import com.foundryvtt.core.documents.Playlist
import kotlin.js.Promise

external class Playlists : WorldCollection<Playlist> {
    val playing: Boolean
    fun initialize(): Promise<Unit>
}