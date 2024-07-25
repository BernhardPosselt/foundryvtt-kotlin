package com.foundryvtt.core.documents

import com.foundryvtt.core.abstract.Document
import js.objects.Record

@JsName("CONFIG.ChatMessage.documentClass")
@Suppress("NAME_CONTAINS_ILLEGAL_CHARS")
external class ChatMessage : Document {
    companion object : DocumentStatic<ChatMessage> {
        fun applyRollMode(data: Any, rollMode: String)
        fun getWhisperRecipients(name: String)
    }

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