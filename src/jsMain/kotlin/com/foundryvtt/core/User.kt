package com.foundryvtt.core

import kotlin.js.Promise


external class User : Document {
    companion object : DocumentStatic<User>

    override fun delete(operation: DatabaseGetOperation): Promise<User>
    override fun update(data: Any, operation: DatabaseGetOperation): Promise<User>

    val isGM: Boolean
    val isBanned: Boolean
    val name: String
    val role: Int
    val password: String
    val passwordSalt: String
    val avatar: String
    val pronouns: String
    // TODO: incomplete
}