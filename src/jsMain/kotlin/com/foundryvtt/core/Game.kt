package com.foundryvtt.core

import com.foundryvtt.core.collections.Actors
import com.foundryvtt.core.collections.Folders
import com.foundryvtt.core.collections.Playlists
import com.foundryvtt.core.collections.Users

external val game: Game

external object Game {
    val settings: Settings
    val actors: Actors?
    val playlists: Playlists?
    val folders: Folders?
    val users: Users
}