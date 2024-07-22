package com.foundryvtt.core

import js.objects.Record
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface ChatMessageData {
    val _id: String?
    val flavor: String?
    val content: String?
    val timestamp: Int?
    val blind: Boolean?
    // incomplete
}

external class ChatMessage : Document {
    companion object : DocumentStatic<ChatMessageData> {
        fun applyRollMode(data: Any, rollMode: String)
        fun getWhisperRecipients(name: String)
    }

    val _rollExpand: Boolean
    val logged: Boolean
    val alias: String
    val isAuthor: Boolean
    val isContentVisible: Boolean
    val isRoll: Boolean
    val visible: Boolean
    fun prepareDerivedData()
    fun applyRollMode(rollMode: String)
    fun getRollData(): Record<String, Any>
}