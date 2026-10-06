package com.foundryvtt.wrappers.update

@TypeSafeUpdateDsl
class ScalarProperty<T>(
    private val path: String
) {
    context(ctx: Context)
    fun set(value: T) {
        val c = ctx.withPath(this.path)
        c.updates[c.currentPath] = value
    }

    context(ctx: Context)
    operator fun unaryMinus() {
        val c = ctx.withPath(this.path)
        c.updates[c.currentPath] = c.deleteObject
    }
}