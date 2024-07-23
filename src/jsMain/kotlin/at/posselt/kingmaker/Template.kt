package at.posselt.kingmaker

import com.foundryvtt.core.loadTemplates
import com.foundryvtt.core.renderTemplate
import js.objects.Record
import js.objects.jso
import kotlinx.coroutines.await

private val distPath = "modules/${Config.moduleId}/dist"

suspend fun loadTpls(paths: Array<String>) {
    val resolvedPaths = paths.map { "$distPath/$it" }.toTypedArray()
    loadTemplates(resolvedPaths).await()
}

suspend fun tpl(path: String, ctx: Record<String, Any?> = jso()): String {
    return renderTemplate("$distPath/$path", ctx).await()
}