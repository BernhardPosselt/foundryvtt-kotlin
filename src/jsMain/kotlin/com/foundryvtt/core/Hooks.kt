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
    fun <T> on(key: String, callback: Function<T>)
    fun <T> once(key: String, callback: Function<T>)
    fun <T> off(key: String, callback: Function<T>)
    fun callAll(key: String, args: Array<Any>)
    fun call(key: String, args: Array<Any>)
    fun onError(location: String, error: Throwable, options: OnErrorOptions = definedExternally)
}

fun <O> Hooks.onReady(callback: (Any) -> O) =
    on("ready", callback)

fun <O> Hooks.onInit(callback: () -> O) =
    on("init", callback)

fun <O> Hooks.onUpdateWorldTime(callback: (worldTime: Int, dt: Int, options: Any, userId: String) -> O) =
    on("updateWorldTime", callback)