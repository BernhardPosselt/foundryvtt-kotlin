package at.posselt.kingmaker

import com.foundryvtt.core.renderTemplate
import js.objects.Record
import js.objects.jso
import kotlinx.coroutines.await

suspend fun tpl(path: String, ctx: Record<String, Any?> = jso()): String {
    return renderTemplate(
        "modules/${Config.moduleId}/dist/$path",
        ctx
    ).await()
}