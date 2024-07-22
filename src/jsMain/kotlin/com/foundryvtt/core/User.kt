package com.foundryvtt.core


external class User : Document {
    companion object : DocumentStatic<Any>

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