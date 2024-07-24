package com.foundryvtt.core

external class Users : WorldCollection<User> {
    val players: Array<User>
    val activeGM: User?

}