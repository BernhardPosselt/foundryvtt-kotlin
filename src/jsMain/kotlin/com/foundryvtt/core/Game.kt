package com.foundryvtt.core

import com.foundryvtt.core.collections.*

external val game: Game

/**
 * Note: many if not all of these objects are only available after init
 */
external object Game {
    val settings: Settings
    val actors: Actors
    val playlists: Playlists
    val folders: Folders
    val users: Users
    val tables: RollTables
    val scenes: Scenes
}