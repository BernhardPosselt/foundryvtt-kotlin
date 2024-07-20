package at.posselt.kingmaker

import com.foundryvtt.core.ChatMessage
import com.foundryvtt.core.ChatMessageData
import kotlinx.coroutines.await

suspend fun postChatMessage(message: String, rollMode: RollMode? = null) {
    val data = ChatMessageData(content = message)
    rollMode?.let { ChatMessage.applyRollMode(data, it.value) }
    ChatMessage.create(data).await()
}