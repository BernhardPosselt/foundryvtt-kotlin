package com.foundryvtt.wrappers.update

import com.foundryvtt.core._del
import com.foundryvtt.wrappers.RecordProperty
import js.objects.Record

fun <T> buildUpdate(properties: T, deleteObject: Any = _del, action: context(Context) T.() -> Unit): Record<String, Any?> {
    val ctx = Context(deleteObject = deleteObject)
    val root = RecordProperty("", properties)
    context(ctx) {
        root {
            properties.action()
        }
    }
    return ctx.updates
}