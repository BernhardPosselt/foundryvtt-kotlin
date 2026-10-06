package com.foundryvtt.wrappers.update

import js.objects.Record
import js.objects.recordOf

class Context(
    val currentPath: String = "",
    val updates: Record<String, Any?> = recordOf(),
    val deleteObject: Any,
) {
    fun withPath(path: String): Context {
        val newPath = if (currentPath == "") path else "$currentPath.$path"
        return Context(
            newPath,
            updates,
            deleteObject
        )
    }
}