package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseDeleteOperation
import com.foundryvtt.core.abstract.DatabaseUpdateOperation
import js.objects.Record
import js.objects.jso
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface ChatSpeakerData {
    val scene: String
    val actor: String
    val token: String
    val alias: String
}


@JsPlainObject
external interface ChatMessageData {
    val _id: String
    val type: String?
    val user: String
    val timestamp: Int
    val flavor: String
    val content: String
    val speaker: ChatSpeakerData
    val whisper: Array<String>
    val blind: Boolean?
    val rolls: Array<String>
    val sound: String
    val emote: Boolean
}

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.ChatMessage.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class ChatMessage : ClientDocument {
    companion object : DocumentStatic<ChatMessage> {
        fun applyRollMode(data: Any, rollMode: String)
        fun getWhisperRecipients(name: String)
    }

    override fun delete(operation: DatabaseDeleteOperation): Promise<ChatMessage>
    override fun update(data: AnyObject, operation: DatabaseUpdateOperation): Promise<ChatMessage>

    var _id: String
    var blind: Boolean
    var content: String
    var emote: Boolean
    var flavor: String
    var logged: Boolean
    var timestamp: Int
    var style: Int
    var type: String

    val _rollExpanded: Boolean

    fun prepareDerivedData()
    fun applyRollMode(rollMode: String)
    fun getRollData(): Record<String, Any>
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun ChatMessage.update(data: ChatMessage, operation: DatabaseUpdateOperation = jso()): Promise<ChatMessage> =
    update(data as AnyObject, operation)