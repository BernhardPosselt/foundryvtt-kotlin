package com.foundryvtt.core

import js.objects.Record
import kotlinx.js.JsPlainObject
import kotlin.js.Promise

@JsPlainObject
external interface ChatMessageData {
    val _id: String?
    val flavor: String?
    val content: String?
    val timestamp: Int?
    val blind: Boolean?
    // incomplete
}

external class ChatMessage : Document<ChatMessageData> {
    companion object {
        fun create(
            data: ChatMessageData,
            operation: DatabaseGetOperation<ChatMessageData> = definedExternally
        ): Promise<ChatMessage>

        fun get(
            id: String,
            operation: DatabaseGetOperation<ChatMessageData> = definedExternally
        ): Promise<ChatMessage?>

        fun applyRollMode(data: Any, rollMode: String)
        fun getWhisperRecipients(name: String)

        // fun getSpeaker
        // fun _getSpeakerFromToken
        // fun _getSpeakerFromActor
        // fun _getSpeakerFromUser
        // fun getSpeakerActor
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
    // fun getHTML(): Promise<jQuery>
}