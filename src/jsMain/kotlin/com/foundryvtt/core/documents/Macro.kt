package com.foundryvtt.core.documents

import com.foundryvtt.core.Actor
import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import js.objects.jso
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.js.JsPlainObject
import kotlin.js.Promise


@JsPlainObject
external interface MacroScope {
    val speaker: ChatSpeakerData
    val actor: Actor
    val token: TokenDocument
    val event: Event
}

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.Macro.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class Macro : ClientDocument {
    companion object : DocumentStatic<Macro>

    override fun delete(operation: DatabaseDeleteOperation): Promise<Macro>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<Macro>

    val isAuthor: Boolean
    val canExecute: Boolean
    val thumbnail: String

    var _id: String
    var name: String
    var type: String
    var author: User
    var img: String
    var scope: String
    var command: String
    var folder: Folder?
    var sort: Int
    var ownership: Ownership

    fun canExecute(user: User): Boolean
    fun execute(scope: MacroScope = definedExternally): ChatMessage
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun Macro.update(data: Macro, operation: DatabaseUpdateOperation = jso()): Promise<Macro> =
    update(data as AnyObject, operation)