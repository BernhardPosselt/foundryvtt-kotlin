package at.posselt.kingmaker

import com.foundryvtt.core.loadTemplates
import com.foundryvtt.core.renderTemplate
import js.objects.Record
import js.objects.jso
import kotlinx.coroutines.await

private const val DIST_PATH = "modules/${Config.MODULE_ID}/dist"

suspend fun loadTpls(paths: Array<Pair<String, String>>) {
    val resolvedPaths = paths.map {
        it.first to "$DIST_PATH/${it.second}"
    }.toRecord()
    loadTemplates(resolvedPaths).await()
}

suspend fun tpl(path: String, ctx: Record<String, Any?> = jso()): String {
    return renderTemplate("$DIST_PATH/$path", ctx).await()
}