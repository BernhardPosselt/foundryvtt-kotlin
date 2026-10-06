package com.foundryvtt.wrappers

import com.foundryvtt.wrappers.update.Context
import com.foundryvtt.wrappers.update.TypeSafeUpdateDsl
import js.objects.Record

@TypeSafeUpdateDsl
open class RecordProperty<T>(
    val path: String,
    val properties: T,
) {
    context(ctx: Context)
    operator fun unaryMinus() {
        val p = ctx.withPath(this.path)
        p.updates[p.currentPath] = p.deleteObject
    }

    context(ctx: Context)
    fun set(value: Record<String, Any?>) {
        val p = ctx.withPath(this.path)
        p.updates[p.currentPath] = value
    }

    context(ctx: Context)
    operator fun set(key: String, value: Any?) {
        val p = ctx.withPath(this.path)
        p.updates["${p.currentPath}.$key"] = value
    }

    context(ctx: Context)
    fun deleteEntry(key: String) {
        val p = ctx.withPath(this.path)
        p.updates["${p.currentPath}.$key"] = p.deleteObject
    }

    context(ctx: Context)
    operator fun invoke(value: context(Context) T.() -> Unit) {
        context(ctx.withPath(this.path)) {
            this.properties.value()
        }
    }
}