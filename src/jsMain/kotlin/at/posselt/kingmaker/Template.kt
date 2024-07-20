package at.posselt.kingmaker

import com.foundryvtt.core.renderTemplate
import kotlinx.coroutines.await

suspend fun <T : Any> tpl(path: String, ctx: T): String {
    val path = "modules/${Config.moduleId}/dist/templates/$path"
    return renderTemplate(
        path,
        ctx
    ).await()
}