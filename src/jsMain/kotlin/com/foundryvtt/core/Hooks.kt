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
    fun on(key: String, callback: Any)
    fun once(key: String, callback: Any)
    fun off(key: String, callback: Any)
    fun callAll(key: String, args: Array<Any>)
    fun call(key: String, args: Array<Any>)
    fun onError(location: String, error: Throwable, options: OnErrorOptions = definedExternally)
}

fun Hooks.onReady(callback: () -> Unit) =
    on("ready", callback)

fun Hooks.onInit(callback: () -> Unit) =
    on("init", callback)

fun Hooks.onUpdateWorldTime(callback: (worldTime: Int, dt: Int, options: Any, userId: String) -> Unit) =
    on("updateWorldTime", callback)