package at.posselt.kingmaker.utils

import at.posselt.kingmaker.RollMode
import com.foundryvtt.core.ChatMessage
import com.foundryvtt.core.ChatMessageData
import js.objects.Record
import js.objects.jso
import kotlinx.coroutines.await

suspend fun postChatTemplate(
    templatePath: String,
    templateContext: Record<String, Any?> = jso(),
    rollMode: RollMode? = null
) {
    val message = tpl(templatePath, templateContext)
    postChatMessage(message, rollMode)
}

suspend fun postChatMessage(message: String, rollMode: RollMode? = null) {
    val data = ChatMessageData(content = message)
    rollMode?.let { ChatMessage.applyRollMode(data, it.value) }
    ChatMessage.create(data).await()
}