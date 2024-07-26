package com.foundryvtt.core

import com.foundryvtt.core.collections.*

external val game: Game

external object Game {
    val settings: Settings
    val actors: Actors?
    val playlists: Playlists?
    val folders: Folders?
    val users: Users
    val tables: RollTables?
}