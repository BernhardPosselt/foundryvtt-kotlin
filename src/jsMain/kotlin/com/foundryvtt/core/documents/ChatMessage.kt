package com.foundryvtt.core.documents

import com.foundryvtt.core.AnyObject
import com.foundryvtt.core.abstract.DatabaseGetOperation
import com.foundryvtt.core.abstract.Document
import js.objects.Record
import js.objects.jso
import kotlin.js.Promise

// required to make instance of work, but since the classes are not registered here
// at page load, we can't use @file:JsQualifier
@JsName("CONFIG.ChatMessage.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class ChatMessage : Document {
    companion object : DocumentStatic<ChatMessage> {
        fun applyRollMode(data: Any, rollMode: String)
        fun getWhisperRecipients(name: String)
    }

    override fun delete(operation: DatabaseGetOperation): Promise<ChatMessage>
    override fun update(data: AnyObject, operation: DatabaseGetOperation): Promise<ChatMessage>

    val blind: Boolean
    val content: String
    val emote: Boolean
    val flavor: String
    val logged: Boolean
    val timestamp: Int
    val style: Int
    val type: String
    val _rollExpanded: Boolean

    fun prepareDerivedData()
    fun applyRollMode(rollMode: String)
    fun getRollData(): Record<String, Any>
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
fun ChatMessage.update(data: ChatMessage, operation: DatabaseGetOperation = jso()): Promise<ChatMessage> =
    update(data as AnyObject, operation)