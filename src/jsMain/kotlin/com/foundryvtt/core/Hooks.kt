package com.foundryvtt.core

import kotlinx.js.JsPlainObject

@JsPlainObject
external interface OnErrorOptions {
    val msg: String?
    val log: String?
    val notify: String?
    val data: Any
}

external object Hooks {
    fun on(key: String, callback: dynamic)
    fun once(key: String, callback: dynamic)
    fun off(key: String, callback: dynamic)
    fun callAll(key: String, args: Array<Any>)
    fun call(key: String, args: Array<Any>)
    fun onError(location: String, error: Throwable, options: OnErrorOptions = definedExternally)
}

fun Hooks.onReady(callback: () -> Any) =
    on("ready", callback)

fun Hooks.onInit(callback: () -> Any) =
    on("init", callback)

fun Hooks.onUpdateWorldTime(callback: (worldTime: Int, dt: Int, options: Any, userId: String) -> Any) =
    on("updateWorldTime", callback)