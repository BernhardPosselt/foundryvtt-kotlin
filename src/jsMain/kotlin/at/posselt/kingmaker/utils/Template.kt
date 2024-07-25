package at.posselt.kingmaker.utils

import at.posselt.kingmaker.Config
import com.foundryvtt.core.loadTemplates
import com.foundryvtt.core.renderTemplate
import js.objects.Record
import js.objects.jso
import kotlinx.coroutines.await

private const val DIST_PATH = "modules/${Config.MODULE_ID}/dist"

fun resolveTemplatePath(path: String) = "$DIST_PATH/$path"

suspend fun loadTpls(paths: Array<Pair<String, String>>) {
    val resolvedPaths = paths.map {
        it.first to resolveTemplatePath(it.second)
    }.toRecord()
    loadTemplates(resolvedPaths).await()
}

suspend fun tpl(path: String, ctx: Record<String, Any?> = jso()): String {
    return renderTemplate("$DIST_PATH/$path", ctx).await()
}