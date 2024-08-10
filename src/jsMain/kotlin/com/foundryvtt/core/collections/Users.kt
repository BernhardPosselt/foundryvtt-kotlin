package com.foundryvtt.core.collections

import com.foundryvtt.core.documents.User

external class Users : WorldCollection<User> {
    companion object : WorldCollectionStatic

    val players: Array<User>
    val activeGM: User?
}