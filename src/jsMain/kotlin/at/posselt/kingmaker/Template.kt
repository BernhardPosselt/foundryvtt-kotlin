package at.posselt.kingmaker

import com.foundryvtt.core.renderTemplate
import js.objects.jso
import kotlinx.coroutines.await

suspend fun tpl(path: String, ctx: Any = jso()): String {
    val path = "modules/${Config.moduleId}/dist/$path"
    return renderTemplate(
        path,
        ctx
    ).await()
}