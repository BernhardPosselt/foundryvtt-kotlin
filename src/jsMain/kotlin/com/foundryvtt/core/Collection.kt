package com.foundryvtt.core

import js.array.JsTuple2
import js.array.ReadonlyArray
import js.iterable.JsIterable
import kotlinx.js.JsPlainObject

@JsPlainObject
external interface GetOptions {
    val strict: Boolean?
}

external class Collection<T>(
    values: ReadonlyArray<JsTuple2<String, T>> = definedExternally,
) : JsIterable<T> {
    val contents: Array<T>
    fun toJSON(): Array<Any>
    fun some(predicate: (T) -> Boolean): Boolean
    fun map(transform: (T) -> T): Collection<T>
    fun reduce(function: (T, T, Int) -> T, initial: T): T
    fun getName(name: String): T?
    fun get(key: String, options: GetOptions = definedExternally): T?
    fun forEach(action: (T) -> Unit)
    fun filter(predicate: (T) -> Boolean): Collection<T>
    fun find(predicate: (T) -> Boolean): T?
}