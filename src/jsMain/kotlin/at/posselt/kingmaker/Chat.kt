package at.posselt.kingmaker

import com.foundryvtt.core.ChatMessage
import com.foundryvtt.core.ChatMessageData
import js.objects.jso
import kotlinx.coroutines.await

suspend fun postChatTemplate(
    templatePath: String,
    templateContext: Any = jso(),
    rollMode: RollMode? = null
) {
    println(templateContext)
    println(JSON.stringify(templateContext))
    val message = tpl(templatePath, templateContext)
    postChatMessage(message, rollMode)
}

suspend fun postChatMessage(message: String, rollMode: RollMode? = null) {
    val data = ChatMessageData(content = message)
    rollMode?.let { ChatMessage.applyRollMode(data, it.value) }
    ChatMessage.create(data).await()
}