package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import com.foundryvtt.core.abstract.Document
import js.objects.jso
import kotlin.js.Promise


// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.User.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class User : Document {
    companion object : DocumentStatic<User>

    override fun delete(operation: DatabaseGetOperation): Promise<User>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<User>

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

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun User.update(data: User, operation: DatabaseGetOperation = jso()): Promise<User> =
    update(data as AnyObject, operation)