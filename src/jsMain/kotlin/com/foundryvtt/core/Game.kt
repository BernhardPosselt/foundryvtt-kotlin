package com.foundryvtt.core

external val game: Game

external object Game {
    val settings: Settings
    val actors: Actors?
    val playlists: Playlists?
    val folders: Folders?
    val users: Users
}