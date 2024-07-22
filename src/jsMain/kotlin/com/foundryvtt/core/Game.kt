package com.foundryvtt.core

external val game: Game

external object Game {
    val settings: Settings
    val actors: Collection<Actor>?
}