package com.foundryvtt.core

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface OnErrorOptions {
    val msg: String?
    val log: String?
    val notify: String?
    val data: Any
}

external interface HooksEventListener {
    fun <T> on(key: String, callback: Function<T>)
}

fun <O> HooksEventListener.onReady(callback: (Any) -> O) =
    on("ready", callback)

fun <O> HooksEventListener.onInit(callback: () -> O) =
    on("init", callback)

fun <O> HooksEventListener.onUpdateWorldTime(callback: (worldTime: Int, dt: Int, options: Any, userId: String) -> O) =
    on("updateWorldTime", callback)

external object Hooks : HooksEventListener {
    override fun <T> on(key: String, callback: Function<T>)
    fun <T> once(key: String, callback: Function<T>)
    fun <T> off(key: String, callback: Function<T>)
    fun callAll(key: String, args: Array<Any>)
    fun call(key: String, args: Array<Any>)
    fun onError(location: String, error: Throwable, options: OnErrorOptions = definedExternally)
}

